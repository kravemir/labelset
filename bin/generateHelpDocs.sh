#!/usr/bin/env bash

SCRIPT_DIRECTORY="$(dirname "$0")"
PROJECT_DIRECTORY="$(dirname "${SCRIPT_DIRECTORY}")"

cd "${PROJECT_DIRECTORY}"

mkdir -p docs docs/src/cli/command

echo -e "# \`labelset\` help\n\n\`\`\`" > docs/help.md
./labelset --help >> docs/help.md
echo -e "\`\`\`" >> docs/help.md

echo -e "\nCommand \`tile\`:\n\`\`\`" >> docs/help.md
./labelset tile --help >> docs/help.md
echo -e "\`\`\`" >> docs/help.md

echo -e "\nCommand \`instance\`:\n\`\`\`" >> docs/help.md
./labelset instance --help >> docs/help.md
echo -e "\`\`\`" >> docs/help.md

echo -e "\nCommand \`project\`:\n\`\`\`" >> docs/help.md
./labelset project --help >> docs/help.md
echo -e "\`\`\`" >> docs/help.md


./labelset --help > docs/src/cli/help.txt
./labelset tile --help > docs/src/cli/command/tile.help.txt
./labelset instance --help > docs/src/cli/command/instance.help.txt
./labelset project --help > docs/src/cli/command/project.help.txt
