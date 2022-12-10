#!/usr/bin/env bash

SCRIPT_DIRECTORY="$(dirname "$0")"
PROJECT_DIRECTORY="$(dirname "${SCRIPT_DIRECTORY}")"

cd "${PROJECT_DIRECTORY}"

mkdir -p docs/cli

./labelset gen-markdown docs/cli
