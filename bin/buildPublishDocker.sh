#!/usr/bin/env bash

# fail on any error!
set -e

# detect script and project directories
SCRIPT_DIRECTORY="$(dirname "$0")"
PROJECT_DIRECTORY="$(dirname "${SCRIPT_DIRECTORY}")"

# perform all actions in project directory
cd "${PROJECT_DIRECTORY}"


# constants
REGISTRY="hub.docker.com"
IMAGE_NAME="kravemir/lablie"

# variables
TAG="$(sed -n '/^version=/{s///; s/\r$//; p; q}' gradle.properties)"
TAG_LATEST="$([[ "$TAG" =~ SNAPSHOT ]] && echo "latest-snapshot" || echo "latest" )"


echo "Releasing: '${IMAGE_NAME}' as tags: '${TAG}', '${TAG_LATEST}'"

docker login
docker build -t "${IMAGE_NAME}:${TAG}" -t "${IMAGE_NAME}:${TAG_LATEST}" .
docker push "${IMAGE_NAME}:${TAG}"
docker push "${IMAGE_NAME}:${TAG_LATEST}"
