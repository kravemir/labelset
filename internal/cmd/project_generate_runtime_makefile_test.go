package cmd

import (
	"path"
	"testing"

	"github.com/kravemir/labelset/internal/assets"

	"gotest.tools/v3/assert"

	_ "embed"
)

//go:embed project_generate_runtime_makefile_test.output.txt
var expectedRuntimeMakefile string

func TestProjectGenerateRuntimeMakefile(t *testing.T) {
	tmpDir := t.TempDir()

	projectFile := path.Join(tmpDir, "test-project-01.json")
	makefileFile := path.Join(tmpDir, "tmp/for/build/Makefile")

	storeStringToFile(t, projectFile, assets.Project01JSON)

	cmd := projectGenerateRuntimeMakefile()
	cmd.SetArgs([]string{
		"test-project-01.json",
		"tmp/for/build/Makefile",
	})

	undoCWDChange := changeCWD(t, tmpDir)
	defer undoCWDChange()

	err := cmd.Execute()
	assert.NilError(t, err)

	makefileContents := loadToString(t, makefileFile)

	assert.Equal(t, expectedRuntimeMakefile, makefileContents)
}
