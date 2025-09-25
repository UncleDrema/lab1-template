#!/usr/bin/env bash
# ci/pull_and_update.sh
# Usage: ./pull_and_update.sh <compose-file-path>
# Example: ./pull_and_update.sh docker-compose.yml
set -euo pipefail

COMPOSE_FILE="${1:-docker-compose.yml}"

if [ ! -f "$COMPOSE_FILE" ]; then
  echo "Compose file not found: $COMPOSE_FILE"
  exit 2
fi

echo "Using compose file: $COMPOSE_FILE"

# Pull updated images defined in compose file
echo "Pulling images..."
docker compose -f "$COMPOSE_FILE" pull

# Recreate containers with new images (no build)
echo "Bringing services up..."
docker compose -f "$COMPOSE_FILE" up -d --no-recreate

# If you want to force recreate (stop/start) change to:
# docker compose -f "$COMPOSE_FILE" up -d --force-recreate

echo "Services status:"
docker compose -f "$COMPOSE_FILE" ps
