package cmd

import (
	"os"
	"path"
	"strings"
	"testing"

	"github.com/kravemir/labelset/internal/assets"

	"gotest.tools/v3/assert"
)

func TestProjectInit(t *testing.T) {
	tmpDir := t.TempDir()

	projectFile := path.Join(tmpDir, "test-project-01.json")
	makefileFile := path.Join(tmpDir, "Makefile")

	storeStringToFile(t, projectFile, assets.Project01JSON)

	cmd := projectInitCmd()

	undoCWDChange := changeCWD(t, tmpDir)
	defer undoCWDChange()

	err := cmd.Execute()
	assert.NilError(t, err)

	makefileContents := loadToString(t, makefileFile)

	assert.Equal(t, strings.Join([]string{
		`LABELSET_BIN := labelset`,
		`export LABELSET_BIN`,
		`TMP_DIR := tmp`,
		`default: all`,
		``,
		`${TMP_DIR}/labelset.Makefile: project.json | ${TMP_DIR}`,
		`	${LABELSET_BIN} project generate-runtime-makefile "$<" "$@"`,
		``,
		`${TMP_DIR}: ;`,
		`	mkdir -p $@`,
		``,
		`include ${TMP_DIR}/labelset.Makefile`,
		``,
	}, "\n"), makefileContents)
}

func loadToString(t *testing.T, filename string) string {
	t.Helper()

	outputContents, err := os.ReadFile(filename)
	assert.NilError(t, err)

	return string(outputContents)
}

func changeCWD(t *testing.T, dir string) func() {
	t.Helper()

	currentDir, err := os.Getwd()
	if err != nil {
		t.Fatalf("get cwd: %v", err)
	}
	reset := func() {
		os.Chdir(currentDir)
	}

	err = os.Chdir(dir)
	if err != nil {
		reset()
		t.Fatalf("change CWD for command execution: %v", err)
	}

	return reset
}
