#!/usr/bin/env bash

SCRIPT_DIRECTORY="$(dirname "$0")"
PROJECT_DIRECTORY="$(dirname "${SCRIPT_DIRECTORY}")"

cd "${PROJECT_DIRECTORY}"

rm -rf docs/src/javadoc
mkdir -p docs/src/javadoc

cp -r library/build/docs/javadoc docs/src/javadoc/library
cp -r tool/build/docs/javadoc docs/src/javadoc/tool

