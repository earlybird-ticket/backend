#!/usr/bin/env bash
set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"

BASE_URL="${BASE_URL:-http://localhost:18086}"
WARMUP_PAYLOAD_NAME="${1:?warmup payload name is required}"
WARMUP_PAYLOAD_PATH="${SCRIPT_DIR}/${WARMUP_PAYLOAD_NAME}"

if [ ! -f "${WARMUP_PAYLOAD_PATH}" ]; then
  echo "warmup payload file not found: ${WARMUP_PAYLOAD_PATH}" >&2
  exit 1
fi

echo "Starting venue warmup..."
echo "Payload: ${WARMUP_PAYLOAD_NAME}"

curl -fsS -X POST \
  "${BASE_URL}/internal/test/warmup" \
  -H "Accept: application/json" \
  -H "Content-Type: application/json" \
  --data @"${WARMUP_PAYLOAD_PATH}"

echo
echo "Venue warmup completed successfully."