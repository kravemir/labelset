#!/usr/bin/env bash

git clean -fdx
gradle wrapper --gradle-version 4.6
snapcraft cleanbuild
