#!/usr/bin/env bash
set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
ROOT_DIR="$(cd "${SCRIPT_DIR}/.." && pwd)"

VENUE_POSTGRES_HOST="${VENUE_POSTGRES_HOST:-localhost}"
VENUE_POSTGRES_PORT="${VENUE_POSTGRES_PORT:-5432}"
VENUE_POSTGRES_DB="${VENUE_POSTGRES_DB:-earlybird}"
VENUE_POSTGRES_USER="${VENUE_POSTGRES_USER:-postgres}"
VENUE_POSTGRES_PASSWORD="${VENUE_POSTGRES_PASSWORD:-postgres}"

export PGPASSWORD="${VENUE_POSTGRES_PASSWORD}"

until pg_isready \
  -h "${VENUE_POSTGRES_HOST}" \
  -p "${VENUE_POSTGRES_PORT}" \
  -d "${VENUE_POSTGRES_DB}" \
  -U "${VENUE_POSTGRES_USER}"; do
  sleep 1
done

psql \
  -h "${VENUE_POSTGRES_HOST}" \
  -p "${VENUE_POSTGRES_PORT}" \
  -d "${VENUE_POSTGRES_DB}" \
  -U "${VENUE_POSTGRES_USER}" \
  -v ON_ERROR_STOP=1 \
  -f "${ROOT_DIR}/docker/venue/init/seed.sql"

unset PGPASSWORD