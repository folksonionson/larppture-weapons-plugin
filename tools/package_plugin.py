#!/usr/bin/env python3
"""Packages dist/classes + plugin resources into the plugin jar."""
import os
import zipfile

ROOT = os.path.dirname(os.path.dirname(os.path.abspath(__file__)))
CLASSES = os.path.join(ROOT, "dist", "classes")
RES = os.path.join(ROOT, "plugin", "src", "main", "resources")
OUT = os.path.join(ROOT, "dist", "LarpptureWeapons-1.0.0.jar")


def main():
    os.makedirs(os.path.dirname(OUT), exist_ok=True)
    count = 0
    with zipfile.ZipFile(OUT, "w", zipfile.ZIP_DEFLATED) as jar:
        for dirpath, _dirs, files in os.walk(CLASSES):
            for name in sorted(files):
                if not name.endswith(".class"):
                    continue
                full = os.path.join(dirpath, name)
                rel = os.path.relpath(full, CLASSES).replace(os.sep, "/")
                jar.write(full, rel)
                count += 1
        for name in ("plugin.yml", "config.yml"):
            jar.write(os.path.join(RES, name), name)
            count += 1
    print("wrote %s (%d entries)" % (OUT, count))


if __name__ == "__main__":
    main()
