package project

import (
	"fmt"
	"sort"
	"strings"

	"github.com/kravemir/labelset/tiling"

	"golang.org/x/exp/maps"
	"golang.org/x/exp/slices"
)

func GenerateRuntimeMakefile(project Project) (string, error) {
	var makefile makefileBuilder

	makefile.appendVariable("OUT_DIR", "output")
	makefile.appendVariable("TMP_DIR", "tmp")

	makefile.append("\n")

	for _, dataSetKey := range sortedKeys(project.Datasets) {
		dataSet := project.Datasets[dataSetKey]
		genImmediateItemsVariable(&makefile, dataSetKey, dataSet)
	}

	makefile.append("\n")

	for _, outputSetKey := range sortedKeys(project.OutputSets) {
		outputSet := project.OutputSets[outputSetKey]

		for _, outputFormat := range outputSet.OutputMimetypes {
			makefile.appendVariable(
				getOutputIdentifier(outputSetKey, outputFormat),
				getOutputsGenerator(project, outputSetKey, outputSet, outputFormat),
			)
		}
	}

	for _, outputSetKey := range sortedKeys(project.OutputSets) {
		outputSet := project.OutputSets[outputSetKey]

		makefile.appendVariable(
			getOutputSetIdentifier(outputSetKey)+"_PAPER",
			buildPaperOptions(outputSet),
		)
	}

	makefile.append("\n")

	var outputTargets []string
	for _, outputSetKey := range sortedKeys(project.OutputSets) {
		outputSet := project.OutputSets[outputSetKey]

		for _, outputFormats := range outputSet.OutputMimetypes {
			outputTargets = append(outputTargets, getOutputVariable(outputSetKey, outputFormats))
		}
	}
	makefile.appendRule("all", strings.Join(outputTargets, " "))

	for _, outputSetKey := range sortedKeys(project.OutputSets) {
		outputSet := project.OutputSets[outputSetKey]

		svgOutputRule := "${TMP_DIR}/" + outputSetKey + "/%.svg"

		if slices.Contains(outputSet.OutputMimetypes, "svg") {
			svgOutputRule = "${OUT_DIR}/" + outputSetKey + "/%.svg"
		}

		generateSvgRule(&makefile, project, outputSetKey, outputSet, svgOutputRule)

		for _, outputFormat := range outputSet.OutputMimetypes {
			switch strings.ToLower(outputFormat) {
			case "svg":
				break
			case "pdf":
				makefile.appendRule(
					"${OUT_DIR}/"+outputSetKey+"/%.pdf",
					svgOutputRule,
					"inkscape --file=\"$<\" --without-gui --export-pdf=\"$@\"",
				)
				break
			default:
				panic(fmt.Errorf("unsupported format %s", outputFormat))
			}
		}
	}

	if len(project.Archives) > 0 {
		var archiveLocations []string
		for _, archive := range project.Archives {
			archiveLocations = append(archiveLocations, getArchiveLocation(archive))
		}

		makefile.appendRule(
			"all_archives",
			strings.Join(archiveLocations, " "),
		)

		for _, archive := range project.Archives {
			if "zip" != archive.Format {
				panic("Only zip archives supported, yet")
			}

			makefile.appendRule(
				getArchiveLocation(archive),
				"all | archives",
				"rm -f \"$@\"; zip -r \"$@\" "+strings.Join(archive.Sources, " "),
			)
		}
	}

	makefile.appendMkdirRule("${OUT_DIR}")
	makefile.appendMkdirRule("archives")

	for _, outputSetKey := range sortedKeys(project.OutputSets) {
		makefile.appendMkdirRule("${OUT_DIR}/" + outputSetKey)
		makefile.appendMkdirRule("${TMP_DIR}/" + outputSetKey)
	}

	makefile.appendRule(
		"information",
		"",
		genInfoCommands(project)...,
	)

	return makefile.builder.String(), nil

}

func generateSvgRule(makefile *makefileBuilder, project Project, outputSetKey string, outputSet OutputSet, svgOutputRule string) {
	srcDataSet := project.Datasets[outputSet.Dataset]

	var srcFile string
	var options strings.Builder

	options.WriteString("$(" + getOutputSetIdentifier(outputSetKey) + "_PAPER) ")

	if srcDataSet.JsonCollectionStorage != (JsonCollectionStorage{}) {
		options.WriteString("--instance-json \"$<\" ")
		srcFile = fmt.Sprintf("%s/%%.json", srcDataSet.JsonCollectionStorage.Location)
	} else if srcDataSet.CSVTableStorage != (CSVTableStorage{}) {
		options.WriteString("--dataset-csv \"$<\" ")
		options.WriteString("--instance \"$*\" ")
		srcFile = fmt.Sprintf("%s", srcDataSet.CSVTableStorage.Location)
	} else {
		panic("No storage defined")
	}

	makefile.appendRule(
		svgOutputRule,
		srcFile+" | ${OUT_DIR}/"+outputSetKey,
		"$(LABELSET_BIN) tile "+options.String()+outputSet.Template+" \"$@\"",
	)
}

func getArchiveLocation(archive Archive) string {
	return fmt.Sprintf("archives/%s.%s", archive.Name, archive.Format)
}

func buildPaperOptions(set OutputSet) string {
	if set.Paper == (tiling.TiledPaper{}) {
		panic("Paper definition expected")
	}

	paper := set.Paper

	return fmt.Sprintf(
		`--paper-size "%.3fx%.3f" --label-offset "%.3f,%.3f" --label-size "%.3fx%.3f" --label-spacing "%.3f,%.3f"`,
		paper.Width, paper.Height,
		paper.TileOffset.X, paper.TileOffset.Y,
		paper.TileSize.Width, paper.TileSize.Height,
		paper.TileSpacing.X, paper.TileSpacing.Y,
	)
}

func genInfoCommands(result Project) []string {
	var commands []string

	for _, dataSetKey := range sortedKeys(result.Datasets) {
		commands = append(commands, fmt.Sprintf("@echo DataSet %s items: $(%s)", dataSetKey, getDatasetIdentifier(dataSetKey)))
	}

	for _, outputSetKey := range sortedKeys(result.OutputSets) {
		outputSet := result.OutputSets[outputSetKey]

		for _, outputFormat := range outputSet.OutputMimetypes {
			commands = append(commands,
				fmt.Sprintf(
					"@echo Outputs %s, %s items: $(%s)",
					outputSetKey, outputFormat, getOutputIdentifier(outputSetKey, outputFormat),
				),
			)
		}
	}

	return commands
}

func genImmediateItemsVariable(makefile *makefileBuilder, dataSetKey string, dataSet DataSet) {
	if dataSet.JsonCollectionStorage != (JsonCollectionStorage{}) {
		makefile.appendVariable(
			getDatasetIdentifier(dataSetKey),
			getSourcesMatcher(dataSet.JsonCollectionStorage),
		)
	} else {
		panic(fmt.Errorf("no storage defined for %s", dataSetKey))
	}
}

func getOutputsGenerator(project Project, outputSetKey string, outputSet OutputSet, outputFormet string) string {
	dataset := project.Datasets[outputSet.Dataset]

	if dataset.JsonCollectionStorage != (JsonCollectionStorage{}) {
		return getOutputsGeneratorForJsonGen(outputSet.Dataset, dataset, outputSetKey, outputFormet)
	} else {
		panic("No storage defined")
	}
}

func getOutputsGeneratorForJsonGen(dataSetKey string, dataSet DataSet, outputSetKey string, outputType string) string {
	return fmt.Sprintf(
		"$(patsubst %s/%%.json, ${OUT_DIR}/%s/%%.%s, ${%s})",
		dataSet.JsonCollectionStorage.Location,
		outputSetKey,
		outputType,
		getDatasetIdentifier(dataSetKey),
	)
}

func getSourcesMatcher(storage JsonCollectionStorage) string {
	return fmt.Sprintf("$(wildcard %s/*.json)", storage.Location)
}

func getDatasetIdentifier(dataSetKey string) string {
	return fmt.Sprintf("DATASET_ITEMS_%s", dataSetKey)
}

func getOutputVariable(outputSetKey string, outputFormat string) string {
	return fmt.Sprintf("${%s}", getOutputIdentifier(outputSetKey, outputFormat))
}

func getOutputIdentifier(outputSetKey string, outputFormat string) string {
	return fmt.Sprintf("%s_%s", getOutputSetIdentifier(outputSetKey), outputFormat)

}

func getOutputSetIdentifier(outputSetKey string) string {
	return fmt.Sprintf("OUTPUTS_%s", outputSetKey)
}

func sortedKeys[M ~map[string]V, V any](m M) []string {
	keys := maps.Keys(m)
	sort.Strings(keys)

	return keys
}
