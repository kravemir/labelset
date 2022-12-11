#!/usr/bin/env bash

SCRIPT_DIRECTORY="$(dirname "$0")"
PROJECT_DIRECTORY="$(dirname "${SCRIPT_DIRECTORY}")"

cd "${PROJECT_DIRECTORY}"

rm -rf docs/content/cli
mkdir -p docs/content/cli

./labelset gen-markdown docs/content/cli
