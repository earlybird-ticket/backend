#!/usr/bin/env bash
set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
ROOT_DIR="$(cd "${SCRIPT_DIR}/.." && pwd)"

SECTION_API_URL="${SECTION_API_URL:?SECTION_API_URL is required}"
OUT_DIR="${OUT_DIR:-${ROOT_DIR}/k6/data/cdn-seats}"
MANIFEST_PATH="${MANIFEST_PATH:-$OUT_DIR/manifest.json}"

mkdir -p "$OUT_DIR"

response_file="$(mktemp)"
curl -fsSL "$SECTION_API_URL" -o "$response_file"

python3 - "$response_file" "$OUT_DIR" "$MANIFEST_PATH" <<'PY'
import json
import os
import subprocess
import sys

response_path = sys.argv[1]
out_dir = sys.argv[2]
manifest_path = sys.argv[3]

with open(response_path, "r", encoding="utf-8") as f:
    payload = json.load(f)

section_list = payload["data"]["section_list"]

manifest = []

for item in section_list:
    section = item["section"].strip()
    url = item["cdn_url"].strip()
    out_file = f"{section}.json"
    out_path = os.path.join(out_dir, out_file)

    print(f"[INFO] section={section!r}")
    print(f"[INFO] url={url!r}")

    try:
        subprocess.run(
            ["curl", "--fail-with-body", "-sSL", url, "-o", out_path],
            check=True
        )
    except subprocess.CalledProcessError:
        print(f"[PY][ERROR] failed section={section!r} url={url!r}", flush=True)
        raise

    manifest.append({
        "section": section,
        "file": out_file,
        "cdn_url": url,
    })

with open(manifest_path, "w", encoding="utf-8") as f:
    json.dump(manifest, f, ensure_ascii=False, indent=2)

print(f"Saved manifest to {manifest_path}")
PY

rm -f "$response_file"

echo "Downloaded CDN seat files into: $OUT_DIR"