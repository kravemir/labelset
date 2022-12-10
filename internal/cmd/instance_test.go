package cmd

import (
	"bytes"
	"os"
	"path"
	"testing"

	"github.com/antchfx/xmlquery"
	"gotest.tools/v3/assert"

	"github.com/kravemir/labelset/internal/assets"
)

func TestInstance(t *testing.T) {
	t.Run("Manual paths", func(t *testing.T) {
		tmpDir := t.TempDir()

		templateFile := path.Join(tmpDir, "template01-custom-path.svg")
		templateDescriptorFile := path.Join(tmpDir, "template01-descriptor-different-path.json")
		instanceFile := path.Join(tmpDir, "instance-data-somwewhere.json")
		outputFile := path.Join(tmpDir, "out-to-be-there.svg")

		storeStringToFile(t, templateFile, assets.Template01)
		storeStringToFile(t, templateDescriptorFile, assets.Template01Descriptor)
		storeStringToFile(t, instanceFile, assets.Instance01JSON)

		cmd := instanceCmd()
		cmd.SetArgs([]string{
			"--template-descriptor",
			templateDescriptorFile,
			"--instance-json",
			instanceFile,
			templateFile,
			outputFile,
		})

		err := cmd.Execute()
		assert.NilError(t, err)

		result := loadXML(t, outputFile)

		assert.Equal(t, 1, len(xmlquery.Find(result, ".//*[@id='nameText']/*[1][text()='JUnit test']")))
		assert.Equal(t, 1, len(xmlquery.Find(result, ".//*[@id='nameText']/*[2][not(text())]")))
		assert.Equal(t, 2, len(xmlquery.Find(result, ".//*[@id='nameText']/*")))
		assert.Equal(t, 1, len(xmlquery.Find(result, ".//*[@id='text4540']/*[text()='Test replacement of texts']")))
		assert.Equal(t, 1, len(xmlquery.Find(result, ".//*[@id='text4544']/*[text()='13. 05. 2017']")))
	})
	t.Run("Default template descriptor path paths", func(t *testing.T) {
		tmpDir := t.TempDir()

		templateFile := path.Join(tmpDir, "template01.svg")
		templateDescriptorFile := path.Join(tmpDir, "template01.labelset.json")
		instanceFile := path.Join(tmpDir, "instance-data-01.json")
		outputFile := path.Join(tmpDir, "out.svg")

		storeStringToFile(t, templateFile, assets.Template01)
		storeStringToFile(t, templateDescriptorFile, assets.Template01Descriptor)
		storeStringToFile(t, instanceFile, assets.Instance01JSON)

		cmd := instanceCmd()
		cmd.SetArgs([]string{
			"--instance-json",
			instanceFile,
			templateFile,
			outputFile,
		})

		err := cmd.Execute()
		assert.NilError(t, err)

		result := loadXML(t, outputFile)

		assert.Equal(t, 1, len(xmlquery.Find(result, ".//*[@id='nameText']/*[1][text()='JUnit test']")))
		assert.Equal(t, 1, len(xmlquery.Find(result, ".//*[@id='nameText']/*[2][not(text())]")))
		assert.Equal(t, 2, len(xmlquery.Find(result, ".//*[@id='nameText']/*")))
		assert.Equal(t, 1, len(xmlquery.Find(result, ".//*[@id='text4540']/*[text()='Test replacement of texts']")))
		assert.Equal(t, 1, len(xmlquery.Find(result, ".//*[@id='text4544']/*[text()='13. 05. 2017']")))
	})
}

func loadXML(t *testing.T, filename string) *xmlquery.Node {
	t.Helper()

	outputContents, err := os.ReadFile(filename)
	assert.NilError(t, err)

	result, err := xmlquery.Parse(bytes.NewReader(outputContents))
	assert.NilError(t, err)

	return result
}

func storeStringToFile(t *testing.T, templateFile string, content string) {
	err := os.WriteFile(templateFile, []byte(content), 0755)
	assert.NilError(t, err)
}
