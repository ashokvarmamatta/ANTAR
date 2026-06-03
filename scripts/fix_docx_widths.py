"""Repair table column widths in the two Play Store .docx files.

Cause of breakage: <w:gridCol w:w="100"/> values are in DXA (twips, 1/20 pt).
A value of 100 twips ≈ 1.7 mm — Word wraps every character. The author seems
to have intended percentage weights (100 / 40 / 30 etc.).

Fix:
- Rescale each <w:tblGrid> proportionally so its columns sum to ~10000 twips
  (just under the 10466-twip usable width on A4 with 720-twip margins).
- Add <w:tblLayout w:type="autofit"/> into each <w:tblPr> so Word can still
  shrink/grow columns to fit content.
- Leave content, styles, and cell shading untouched.
"""

import io
import re
import shutil
import zipfile
from pathlib import Path

TARGET_TOTAL = 10000  # twips, leaves a tiny gutter against the 10466 usable width
DOCX_FILES = [
    "ANTAR_PlayStore_Assets.docx",
    "ANTAR_PlayStore_Audit.docx",
]


def rescale_grid(match: re.Match) -> str:
    inner = match.group(1)
    cols = re.findall(r'<w:gridCol\s+w:w="(\d+)"\s*/>', inner)
    if not cols:
        return match.group(0)
    weights = [int(w) for w in cols]
    total = sum(weights) or len(weights)
    new_widths = [max(600, round(w / total * TARGET_TOTAL)) for w in weights]
    # nudge last column so the sum lands exactly on TARGET_TOTAL
    new_widths[-1] += TARGET_TOTAL - sum(new_widths)
    rebuilt = "".join(f'<w:gridCol w:w="{w}"/>' for w in new_widths)
    return f"<w:tblGrid>{rebuilt}</w:tblGrid>"


def add_autofit(tblPr_block: str) -> str:
    """Insert <w:tblLayout w:type="autofit"/> after <w:tblW .../> if not present."""
    if "<w:tblLayout" in tblPr_block:
        return tblPr_block
    return re.sub(
        r"(<w:tblW[^/]*/>)",
        r'\1<w:tblLayout w:type="autofit"/>',
        tblPr_block,
        count=1,
    )


def fix_xml(xml: str) -> str:
    xml = re.sub(r"<w:tblGrid>(.*?)</w:tblGrid>", rescale_grid, xml, flags=re.DOTALL)
    xml = re.sub(
        r"<w:tblPr>(.*?)</w:tblPr>",
        lambda m: "<w:tblPr>" + add_autofit(m.group(1)) + "</w:tblPr>",
        xml,
        flags=re.DOTALL,
    )
    return xml


def repair(src: Path):
    backup = src.with_suffix(src.suffix + ".bak")
    if not backup.exists():
        shutil.copy2(src, backup)
        print(f"  backup -> {backup.name}")

    buf = io.BytesIO()
    with zipfile.ZipFile(src, "r") as zin, zipfile.ZipFile(
        buf, "w", zipfile.ZIP_DEFLATED
    ) as zout:
        for item in zin.infolist():
            data = zin.read(item.filename)
            if item.filename == "word/document.xml":
                xml = data.decode("utf-8")
                fixed = fix_xml(xml)
                changed = fixed != xml
                data = fixed.encode("utf-8")
                print(f"  document.xml rewritten: {changed}")
            zout.writestr(item, data)

    src.write_bytes(buf.getvalue())
    print(f"  wrote {src} ({src.stat().st_size} bytes)")


if __name__ == "__main__":
    root = Path(__file__).resolve().parent.parent
    for name in DOCX_FILES:
        path = root / name
        if not path.exists():
            print(f"skip (missing): {path}")
            continue
        print(f"fixing {name}")
        repair(path)
