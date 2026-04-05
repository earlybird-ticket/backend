#!/usr/bin/env bash
set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
ROOT_DIR="$(cd "${SCRIPT_DIR}/.." && pwd)"

USER_TOKENS_PATH="${USER_TOKENS_PATH:-${ROOT_DIR}/k6/data/user_tokens.json}"

if [ ! -f "${USER_TOKENS_PATH}" ]; then
  echo "user tokens file not found: ${USER_TOKENS_PATH}" >&2
  exit 1
fi

k6 run \
  -e BASE_URL="${BASE_URL:-http://localhost:18086}" \
  -e VERSION="${VERSION:-local}" \
  -e USER_TOKENS_PATH="${USER_TOKENS_PATH}" \
  -e GRAFANA_URL="${GRAFANA_URL:-}" \
  -e START_CHAR="${START_CHAR:-A}" \
  -e END_CHAR="${END_CHAR:-Z}" \
  "${ROOT_DIR}/k6/scenarios/venue-performance-test.js"