#!/usr/bin/env bash

SCRIPT_DIRECTORY="$(dirname "$0")"
PROJECT_DIRECTORY="$(dirname "${SCRIPT_DIRECTORY}")"

cd "${PROJECT_DIRECTORY}"

rm -rf docs/content/cli
mkdir -p docs/content/cli

go run ./main.go gen-markdown docs/content/cli

echo -e '---\ntitle: CLI man\nweight: 40\nbookCollapseSection: true\n---' >> docs/content/cli/_index.md
