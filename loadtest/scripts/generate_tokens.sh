#!/usr/bin/env bash
set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
ROOT_DIR="$(cd "${SCRIPT_DIR}/.." && pwd)"

AUTH_BASE_URL="${AUTH_BASE_URL:-http://localhost:18082}"
OUTPUT_PATH="${OUTPUT_PATH:-${ROOT_DIR}/k6/data/user_tokens.json}"
TOTAL_COUNT="${TOTAL_COUNT:-1000}"
BATCH_SIZE="${BATCH_SIZE:-200}"

mkdir -p "$(dirname "${OUTPUT_PATH}")"
TMP_DIR="$(mktemp -d)"
trap 'rm -rf "${TMP_DIR}"' EXIT

echo "[]" > "${OUTPUT_PATH}"

start=0
batch_index=0

while [ "${start}" -lt "${TOTAL_COUNT}" ]; do
  remaining=$((TOTAL_COUNT - start))
  count="${BATCH_SIZE}"
  if [ "${remaining}" -lt "${BATCH_SIZE}" ]; then
    count="${remaining}"
  fi

  response_file="${TMP_DIR}/batch_${batch_index}.json"

  curl -fsS -X POST \
    "${AUTH_BASE_URL}/test/generate-tokens?start=${start}&count=${count}" \
    -H "Accept: application/json" \
    -o "${response_file}"

  python3 - "${OUTPUT_PATH}" "${response_file}" <<'PY'
import json
import sys

output_path = sys.argv[1]
response_path = sys.argv[2]

with open(output_path, "r", encoding="utf-8") as f:
    existing = json.load(f)

with open(response_path, "r", encoding="utf-8") as f:
    response = json.load(f)

data = response.get("data")
if not isinstance(data, list):
    raise SystemExit(f"Invalid response format: {response_path}")

merged = existing + data

with open(output_path, "w", encoding="utf-8") as f:
    json.dump(merged, f, ensure_ascii=False, indent=2)
PY

  start=$((start + count))
  batch_index=$((batch_index + 1))
done

echo "Generated token file: ${OUTPUT_PATH}"
echo "Total users: ${TOTAL_COUNT}"