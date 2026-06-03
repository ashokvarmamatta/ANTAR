"""One-shot converter for the two Play Store .docx files into colorful Markdown.

Reads the .docx as a zip, walks word/document.xml, and emits Markdown with:
- shields.io badges in the header
- emoji per top-level section
- GitHub-flavored callouts (> [!NOTE] / [!TIP] / [!WARNING] / [!IMPORTANT])
- inline HTML <span> color tags for highlights (renders outside GitHub too)
"""

import io
import re
import sys
import zipfile
import xml.etree.ElementTree as ET
from pathlib import Path

W = "http://schemas.openxmlformats.org/wordprocessingml/2006/main"
NS = {"w": W}
T_TAG = f"{{{W}}}t"
BR_TAG = f"{{{W}}}br"
TAB_TAG = f"{{{W}}}tab"
R_TAG = f"{{{W}}}r"
P_TAG = f"{{{W}}}p"
RPR_TAG = f"{{{W}}}rPr"


def _run_signature(r):
    """Cheap fingerprint of a run's formatting — bold/italic/size/color."""
    rpr = r.find(f"{{{W}}}rPr")
    if rpr is None:
        return ""
    keys = []
    for child in rpr:
        local = child.tag.split("}")[-1]
        val = child.get(f"{{{W}}}val", "")
        keys.append(f"{local}={val}")
    return "|".join(sorted(keys))


def _runs_text(el):
    """Concatenate run text, inserting \\n when formatting changes between adjacent runs
    that aren't already separated by whitespace — Word users often use bold/non-bold
    transitions to visually break lines without an actual <w:br>."""
    out = []
    prev_sig = None
    prev_tail = ""
    for r in el.iter(R_TAG):
        sig = _run_signature(r)
        parts = []
        for node in r.iter():
            if node.tag == T_TAG:
                parts.append(node.text or "")
            elif node.tag == BR_TAG:
                parts.append("\n")
            elif node.tag == TAB_TAG:
                parts.append("\t")
        run_text = "".join(parts)
        if not run_text:
            continue
        head = run_text[:1]
        # Insert a break if formatting changed and there's no whitespace at the seam.
        needs_break = (
            prev_sig is not None
            and sig != prev_sig
            and prev_tail
            and not prev_tail.isspace()
            and not head.isspace()
            # only when the seam looks like it should be a new line:
            # bold-label → body, or end-of-sentence → next sentence
            and (
                prev_tail in ".:;)" + chr(0xA0)
                or prev_tail.isalpha() and head.isdigit()
                or prev_tail.isupper() and head.isupper() is False and prev_tail.isalpha()
                or head.isupper() and prev_tail.islower()
            )
        )
        if needs_break:
            out.append("\n")
        out.append(run_text)
        prev_sig = sig
        prev_tail = run_text[-1]
    text = "".join(out).strip()
    text = re.sub(r"[ \t]+\n", "\n", text)
    text = re.sub(r"\n{3,}", "\n\n", text)
    return text


def text_of(el):
    """Extract text from a paragraph, table cell, or arbitrary block.

    Cells can hold multiple <w:p> children — join those with newlines so the
    Markdown reflects the original line breaks.
    """
    # If this element directly contains <w:p> children (e.g. <w:tc>), join them.
    paragraphs = el.findall("w:p", NS)
    if paragraphs:
        parts = [_runs_text(p) for p in paragraphs]
        return "\n".join(s for s in parts if s)
    return _runs_text(el)


def parse_docx(path):
    with zipfile.ZipFile(path) as z:
        xml = z.read("word/document.xml")
    root = ET.fromstring(xml)
    body = root.find("w:body", NS)
    blocks = []
    for el in body:
        tag = el.tag.split("}")[-1]
        if tag == "p":
            pPr = el.find("w:pPr", NS)
            style = ""
            is_list = False
            if pPr is not None:
                ps = pPr.find("w:pStyle", NS)
                if ps is not None:
                    style = ps.get(f"{{{W}}}val", "")
                if pPr.find("w:numPr", NS) is not None:
                    is_list = True
            blocks.append(("p", style, is_list, text_of(el)))
        elif tag == "tbl":
            rows = []
            for tr in el.findall("w:tr", NS):
                cells = [text_of(tc) for tc in tr.findall("w:tc", NS)]
                rows.append(cells)
            blocks.append(("tbl", "", False, rows))
    return blocks


# ---------- color / emoji helpers ----------

SECTION_EMOJI = {
    "store listing": "🛍️",
    "categorisation": "🗂️",
    "categorization": "🗂️",
    "keywords": "🔑",
    "asset": "🎨",
    "graphic": "🖼️",
    "icon": "🟢",
    "screenshot": "📱",
    "release": "🚀",
    "data safety": "🛡️",
    "permission": "🔐",
    "privacy": "🔒",
    "ads": "📢",
    "target audience": "👥",
    "summary": "📋",
    "verdict": "✅",
    "compliance": "⚖️",
    "policy": "📜",
    "review": "🔍",
    "audit": "🔎",
    "checklist": "✔️",
    "branding": "✨",
    "metadata": "🏷️",
    "feature": "⭐",
    "risk": "⚠️",
    "blocker": "🛑",
    "action": "🎯",
    "next steps": "👣",
}

CALLOUT_HINTS = [
    (r"\b(warning|risk|blocker|reject|fail|critical)\b", "WARNING"),
    (r"\b(caution|policy violation|sensitive)\b", "CAUTION"),
    (r"\b(important|must|required|mandatory)\b", "IMPORTANT"),
    (r"\b(tip|recommendation|suggested|consider)\b", "TIP"),
    (r"\b(note|info|notice|fyi)\b", "NOTE"),
]


def emoji_for(heading: str) -> str:
    low = heading.lower()
    for key, emo in SECTION_EMOJI.items():
        if key in low:
            return emo
    return "📌"


def colorize_inline(text: str) -> str:
    """Highlight short status words with HTML color spans."""
    rules = [
        (r"\b(PASS|OK|APPROVED|READY|YES|DONE|COMPLETE)\b", "#22c55e"),
        (r"\b(FAIL|FAILED|BLOCKED|REJECT(?:ED)?|NO|MISSING|TODO)\b", "#ef4444"),
        (r"\b(WARN|WARNING|RISK|REVIEW|PARTIAL|PENDING)\b", "#f59e0b"),
        (r"\b(INFO|NOTE|N/?A|OPTIONAL)\b", "#3b82f6"),
    ]
    for pat, color in rules:
        text = re.sub(
            pat,
            lambda m, c=color: f'<span style="color:{c}"><b>{m.group(0)}</b></span>',
            text,
        )
    return text


def md_table(rows):
    if not rows:
        return ""
    width = max(len(r) for r in rows)
    rows = [r + [""] * (width - len(r)) for r in rows]

    def esc(s):
        return s.replace("|", "\\|").replace("\n", "<br>")

    header = rows[0]
    out = ["| " + " | ".join(esc(c) for c in header) + " |"]
    out.append("|" + "|".join(["---"] * width) + "|")
    for r in rows[1:]:
        out.append("| " + " | ".join(esc(c) for c in r) + " |")
    return "\n".join(out)


def callout_for(text: str):
    low = text.lower()
    for pat, kind in CALLOUT_HINTS:
        if re.search(pat, low):
            return kind
    return None


# ---------- main converter ----------

HEADER_BADGES = """
![Play Store](https://img.shields.io/badge/Google%20Play-Listing-34A853?logo=googleplay&logoColor=white)
![Android](https://img.shields.io/badge/Android-12%2B-3DDC84?logo=android&logoColor=white)
![Package](https://img.shields.io/badge/package-com.ashes.dev.works.system.core.internals.antar-0EA5E9)
![Version](https://img.shields.io/badge/version-1.0%20(1)-8B5CF6)
![Status](https://img.shields.io/badge/status-Pre--Submission-F59E0B)
"""


def convert(docx_path: Path, md_path: Path, title: str, subtitle: str):
    blocks = parse_docx(docx_path)
    lines = []
    lines.append(f"# {emoji_for(title)} {title}")
    lines.append("")
    lines.append(f"> _{subtitle}_")
    lines.append("")
    lines.append(HEADER_BADGES.strip())
    lines.append("")
    lines.append("---")
    lines.append("")

    saw_first_heading = False
    pending_list = []

    def flush_list():
        nonlocal pending_list
        if pending_list:
            lines.extend(pending_list)
            lines.append("")
            pending_list = []

    for kind, style, is_list, content in blocks:
        if kind == "tbl":
            flush_list()
            # docx often wraps a single-cell title or callout as a 1x1 table
            rows = content
            if len(rows) == 1 and len(rows[0]) == 1:
                t = rows[0][0]
                if not t:
                    continue
                callout = callout_for(t)
                if callout:
                    lines.append(f"> [!{callout}]")
                    for ln in t.split("\n"):
                        lines.append(f"> {colorize_inline(ln)}" if ln.strip() else ">")
                    lines.append("")
                else:
                    for ln in t.split("\n"):
                        lines.append(f"> {colorize_inline(ln)}" if ln.strip() else ">")
                    lines.append("")
            else:
                lines.append(md_table([[colorize_inline(c) for c in r] for r in rows]))
                lines.append("")
            continue

        text = content
        if not text and not is_list:
            flush_list()
            continue

        if style.startswith("Heading"):
            flush_list()
            level = re.search(r"\d+", style)
            n = int(level.group(0)) if level else 2
            n = max(2, min(n + 1, 6))  # H1 reserved for doc title
            prefix = "#" * n
            emo = emoji_for(text)
            lines.append("")
            if not saw_first_heading and n == 2:
                lines.append("---")
                lines.append("")
                saw_first_heading = True
            lines.append(f"{prefix} {emo} {text}")
            lines.append("")
            continue

        if is_list:
            pending_list.append(f"- {colorize_inline(text)}")
            continue

        flush_list()
        callout = callout_for(text) if len(text) < 400 else None
        if callout and len(text) < 600:
            lines.append(f"> [!{callout}]")
            for ln in text.split("\n"):
                lines.append(f"> {colorize_inline(ln)}" if ln.strip() else ">")
            lines.append("")
        else:
            for chunk in re.split(r"\n\n+", text):
                for ln in chunk.split("\n"):
                    lines.append(colorize_inline(ln))
                lines.append("")

    flush_list()
    lines.append("")
    lines.append("---")
    lines.append("")
    lines.append(
        '<sub>🎨 Rendered with color highlights — '
        '<span style="color:#22c55e">green = pass</span>, '
        '<span style="color:#ef4444">red = blocker</span>, '
        '<span style="color:#f59e0b">amber = review</span>, '
        '<span style="color:#3b82f6">blue = info</span>.</sub>'
    )
    lines.append("")

    md_path.write_text("\n".join(lines), encoding="utf-8")
    print(f"wrote {md_path} ({md_path.stat().st_size} bytes)")


if __name__ == "__main__":
    root = Path(__file__).resolve().parent.parent
    convert(
        root / "ANTAR_PlayStore_Assets.docx",
        root / "ANTAR_PlayStore_Assets.md",
        "ANTAR — Play Store Publishing Assets",
        "Copy-paste kit for the Google Play Console listing.",
    )
    convert(
        root / "ANTAR_PlayStore_Audit.docx",
        root / "ANTAR_PlayStore_Audit.md",
        "ANTAR — Play Store Readiness Audit",
        "Pre-submission audit of policies, permissions, and metadata.",
    )
