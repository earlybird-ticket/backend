#!/usr/bin/env bash
set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
ROOT_DIR="$(cd "${SCRIPT_DIR}/.." && pwd)"

SCENARIO_NAME="${1:?scenario name is required}"
SCENARIO_PATH="${ROOT_DIR}/k6/scenarios/${SCENARIO_NAME}.js"

USER_TOKENS_PATH="${USER_TOKENS_PATH:-${ROOT_DIR}/k6/data/user_tokens.json}"
CDN_MANIFEST_PATH="${CDN_MANIFEST_PATH:-${ROOT_DIR}/k6/data/cdn-seats/manifest.json}"

if [ ! -f "${USER_TOKENS_PATH}" ]; then
  echo "user tokens file not found: ${USER_TOKENS_PATH}" >&2
  exit 1
fi

if [ ! -f "${SCENARIO_PATH}" ]; then
  echo "scenario file not found: ${SCENARIO_PATH}" >&2
  exit 1
fi


k6 run \
  -e BASE_URL="${BASE_URL:-http://localhost:18086}" \
  -e VERSION="${VERSION:-local}" \
  -e USER_TOKENS_PATH="${USER_TOKENS_PATH}" \
  -e CDN_MANIFEST_PATH="${CDN_MANIFEST_PATH}" \
  -e GRAFANA_URL="${GRAFANA_URL:-}" \
  -e START_CHAR="${START_CHAR:-A}" \
  -e END_CHAR="${END_CHAR:-Z}" \
  "${SCENARIO_PATH}"