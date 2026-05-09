#!/usr/bin/env python3
"""
Minimal .docx structural validator.

Usage:
    python scripts/office/validate.py [path/to/file.docx]

Validates that the file is a well-formed OOXML container that Microsoft
Word and Google Docs will open without complaint:
- File exists and is a valid ZIP
- Contains the required parts: [Content_Types].xml, _rels/.rels,
  word/document.xml
- Each XML part parses without error
- word/document.xml has the expected w:document root
"""
from __future__ import annotations

import sys
import zipfile
from pathlib import Path
from xml.etree import ElementTree as ET

REQUIRED_PARTS = (
    "[Content_Types].xml",
    "_rels/.rels",
    "word/document.xml",
)
W_NS = "{http://schemas.openxmlformats.org/wordprocessingml/2006/main}"


def fail(msg: str) -> None:
    print(f"  FAIL: {msg}")
    sys.exit(1)


def ok(msg: str) -> None:
    print(f"  OK  : {msg}")


def main() -> int:
    if len(sys.argv) < 2:
        # Default: look in project root for any *_PlayStore_Audit.docx
        root = Path(__file__).resolve().parents[2]
        matches = sorted(root.glob("*_PlayStore_Audit.docx"))
        if not matches:
            fail(f"No *_PlayStore_Audit.docx found in {root}")
        path = matches[0]
    else:
        path = Path(sys.argv[1]).resolve()

    print(f"Validating: {path}")

    if not path.exists():
        fail(f"File does not exist: {path}")
    if path.stat().st_size == 0:
        fail("File is empty")
    ok(f"File present ({path.stat().st_size:,} bytes)")

    if not zipfile.is_zipfile(path):
        fail("Not a valid ZIP/OOXML container")
    ok("Valid ZIP container")

    with zipfile.ZipFile(path) as zf:
        names = set(zf.namelist())
        for part in REQUIRED_PARTS:
            if part not in names:
                fail(f"Required part missing: {part}")
        ok(f"All {len(REQUIRED_PARTS)} required parts present")

        for part in (p for p in names if p.endswith(".xml") or p.endswith(".rels")):
            try:
                ET.fromstring(zf.read(part))
            except ET.ParseError as exc:
                fail(f"XML parse error in {part}: {exc}")
        ok("All XML parts parse cleanly")

        doc_root = ET.fromstring(zf.read("word/document.xml"))
        if doc_root.tag != f"{W_NS}document":
            fail(f"word/document.xml has unexpected root: {doc_root.tag}")
        ok("word/document.xml has correct root element")

    print("PASS — document is structurally valid.")
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
