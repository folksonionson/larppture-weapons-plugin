#!/usr/bin/env python3
"""Zips the resourcepack/ source tree into a distributable pack and prints
its sha-1 (paste it into the plugin's resource-pack.sha1 config)."""
import hashlib
import os
import zipfile

ROOT = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
PACK = os.path.join(ROOT, "resourcepack")
OUT = os.path.join(ROOT, "dist", "LarpptureWeapons-Pack-1.0.0.zip")


def main():
    os.makedirs(os.path.dirname(OUT), exist_ok=True)
    count = 0
    with zipfile.ZipFile(OUT, "w", zipfile.ZIP_DEFLATED) as zipf:
        for dirpath, dirs, files in os.walk(PACK):
            dirs.sort()
            for name in sorted(files):
                full = os.path.join(dirpath, name)
                rel = os.path.relpath(full, PACK).replace(os.sep, "/")
                zipf.write(full, rel)
                count += 1
    digest = hashlib.sha1()
    with open(OUT, "rb") as fh:
        for chunk in iter(lambda: fh.read(65536), b""):
            digest.update(chunk)
    print("wrote %s (%d files)" % (OUT, count))
    print("sha1: %s" % digest.hexdigest())


if __name__ == "__main__":
    main()
