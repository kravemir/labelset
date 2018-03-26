#!/usr/bin/env bash

SCRIPT_DIRECTORY="$(dirname "$0")"
PROJECT_DIRECTORY="$(dirname "${SCRIPT_DIRECTORY}")"

cd "${PROJECT_DIRECTORY}"

TOOL_JAR=`find ./tool/build/libs -name '*-executable.jar'`

echo -e "# \`lablie\` help\n\n\`\`\`" > docs/help.md
java -jar  "${TOOL_JAR}" --help >> docs/help.md
echo -e "\`\`\`" >> docs/help.md

echo -e "\nCommand \`tile\`:\n\`\`\`" >> docs/help.md
java -jar  "${TOOL_JAR}" tile --help >> docs/help.md
echo -e "\`\`\`" >> docs/help.md

echo -e "\nCommand \`instance\`:\n\`\`\`" >> docs/help.md
java -jar  "${TOOL_JAR}" instance --help >> docs/help.md
echo -e "\`\`\`" >> docs/help.md

echo -e "\nCommand \`project\`:\n\`\`\`" >> docs/help.md
java -jar  "${TOOL_JAR}" project --help >> docs/help.md
echo -e "\`\`\`" >> docs/help.md

echo -e "\nCommand \`project generate-makefile\`:\n\`\`\`" >> docs/help.md
java -jar  "${TOOL_JAR}" project generate-makefile --help >> docs/help.md
echo -e "\`\`\`" >> docs/help.md


java -jar  "${TOOL_JAR}" --help > docs/src/cli/help.txt
java -jar  "${TOOL_JAR}" tile --help > docs/src/cli/command/tile.help.txt
java -jar  "${TOOL_JAR}" instance --help > docs/src/cli/command/instance.help.txt
java -jar  "${TOOL_JAR}" project --help > docs/src/cli/command/project.help.txt
