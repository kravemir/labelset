package assets

import (
	_ "embed"
	"encoding/json"
)

//go:embed label01.svg
var Label01 string

//go:embed label02.svg
var Label02 string

//go:embed template01.svg
var Template01 string

//go:embed template01.labelset.json
var Template01Descriptor string

//go:embed template02.svg
var Template02 string

//go:embed template02.labelset.json
var Template02Descriptor string

//go:embed test-instance.01.json
var Instance01JSON string

var Instance01 = loadValues(Instance01JSON)

//go:embed test-instance.02.json
var Instance02JSON string

var Instance02 = loadValues(Instance02JSON)

//go:embed test-project.01.json
var Project01JSON string

func loadValues(text string) map[string]any {
	result := map[string]any{}

	json.Unmarshal([]byte(text), &result)

	return result
}
