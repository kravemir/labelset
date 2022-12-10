package template_test

import (
	"testing"

	"github.com/antchfx/xmlquery"
	"gotest.tools/v3/assert"

	"github.com/kravemir/labelset/internal/assets"
	"github.com/kravemir/labelset/template"
)

func TestTemplate_Render(t *testing.T) {
	t.Run("Should use instance content", func(t *testing.T) {
		tmpl, err := template.NewTemplate(assets.Template01, assets.Template01Descriptor)
		assert.NilError(t, err)

		result, err := tmpl.Render(assets.Instance01)
		assert.NilError(t, err)

		assert.Equal(t, 1, len(xmlquery.Find(result, ".//*[@id='nameText']/*[1][text()='JUnit test']")))
		assert.Equal(t, 1, len(xmlquery.Find(result, ".//*[@id='nameText']/*[2][text()='']")))
		assert.Equal(t, 2, len(xmlquery.Find(result, ".//*[@id='nameText']/*")))
		assert.Equal(t, 1, len(xmlquery.Find(result, ".//*[@id='text4540']/*[text()='Test replacement of texts']")))
		assert.Equal(t, 1, len(xmlquery.Find(result, ".//*[@id='text4544']/*[text()='13. 05. 2017']")))
	})
	t.Run("Should split multiline string to tspans", func(t *testing.T) {
		tmpl, err := template.NewTemplate(assets.Template01, assets.Template01Descriptor)
		assert.NilError(t, err)

		result, err := tmpl.Render(assets.Instance02)
		assert.NilError(t, err)

		assert.Equal(t, 1, len(xmlquery.Find(result, ".//*[@id='nameText']/*[1][text()='Line no. 01']")))
		assert.Equal(t, 1, len(xmlquery.Find(result, ".//*[@id='nameText']/*[2][text()='.. line no 02 ..']")))
		assert.Equal(t, 2, len(xmlquery.Find(result, ".//*[@id='nameText']/*")))
		assert.Equal(t, 1, len(xmlquery.Find(result, ".//*[@id='text4540']/*[text()='Test replacement of texts']")))
		assert.Equal(t, 1, len(xmlquery.Find(result, ".//*[@id='text4544']/*[text()='13. 05. 2017']")))
	})
}

func TestMultipleReplacements(t *testing.T) {
	tests := []struct {
		textSize       string
		contentMatches map[string]int
	}{
		{
			"unknown",
			map[string]int{
				"//*[@id='text-large']/*[1][text()='']":  1,
				"//*[@id='text-large']/*[2][text()='']":  1,
				"//*[@id='text-medium']/*[1][text()='']": 1,
				"//*[@id='text-medium']/*[2][text()='']": 1,
				"//*[@id='text-small']/*[1][text()='']":  1,
				"//*[@id='text-small']/*[2][text()='']":  1,
			}},
		{
			"large",
			map[string]int{
				"//*[@id='text-large']/*[1][text()='Some multi-line']": 1,
				"//*[@id='text-large']/*[2][text()='text']":            1,
				"//*[@id='text-medium']/*[1][text()='']":               1,
				"//*[@id='text-medium']/*[2][text()='']":               1,
				"//*[@id='text-small']/*[1][text()='']":                1,
				"//*[@id='text-small']/*[2][text()='']":                1,
			}},
		{
			"medium",
			map[string]int{
				"//*[@id='text-large']/*[1][text()='']":                 1,
				"//*[@id='text-large']/*[2][text()='']":                 1,
				"//*[@id='text-medium']/*[1][text()='Some multi-line']": 1,
				"//*[@id='text-medium']/*[2][text()='text']":            1,
				"//*[@id='text-small']/*[1][text()='']":                 1,
				"//*[@id='text-small']/*[2][text()='']":                 1,
			}},
		{
			"small",
			map[string]int{
				"//*[@id='text-large']/*[1][text()='']":                1,
				"//*[@id='text-large']/*[2][text()='']":                1,
				"//*[@id='text-medium']/*[1][text()='']":               1,
				"//*[@id='text-medium']/*[2][text()='']":               1,
				"//*[@id='text-small']/*[1][text()='Some multi-line']": 1,
				"//*[@id='text-small']/*[2][text()='text']":            1,
			}},
	}

	for _, test := range tests {
		tmpl, err := template.NewTemplate(assets.Template02, assets.Template02Descriptor)
		assert.NilError(t, err)

		t.Run(test.textSize, func(t *testing.T) {
			result, err := tmpl.Render(map[string]any{
				"text":     "Some multi-line\ntext",
				"TextSize": test.textSize,
			})
			assert.NilError(t, err)

			for matcher, expectedCount := range test.contentMatches {
				actualCount := len(xmlquery.Find(result, matcher))

				assert.Equal(
					t, actualCount, expectedCount,
					"count(%s) = %d; not %d", matcher, actualCount, expectedCount,
				)
			}
		})
	}
}
