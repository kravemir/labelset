#!/usr/bin/env bash

# fail on any error!
set -e

# detect script and project directories
SCRIPT_DIRECTORY="$(dirname "$0")"
PROJECT_DIRECTORY="$(dirname "${SCRIPT_DIRECTORY}")"

# perform all actions in project directory
cd "${PROJECT_DIRECTORY}"

# build, login, and publish!
snapcraft cleanbuild
snapcraft login
snapcraft push --release=edge lablie_UNDEFINED_amd64.snap
