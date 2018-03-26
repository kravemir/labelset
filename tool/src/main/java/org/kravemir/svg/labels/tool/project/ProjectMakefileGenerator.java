package org.kravemir.svg.labels.tool.project;

import org.kravemir.svg.labels.model.TiledPaper;
import org.kravemir.svg.labels.tool.model.Project;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.apache.commons.lang3.Validate.isTrue;

public class ProjectMakefileGenerator {

    public String generateMakefile(String source) {
        MakefileBuilder makefile = new MakefileBuilder();

        if (isNotBlank(System.getenv("LABLIE_BIN"))) {
            makefile.appendVariable("LABLIE_BIN", System.getenv("LABLIE_BIN"), true);
        } else {
            makefile.appendVariable("LABLIE_BIN", "lablie", true);
        }

        makefile.appendVariable("TMP_DIR", "tmp");

        makefile.appendRule("default", "all");

        makefile.appendRule(
                "${TMP_DIR}/lablie.Makefile",
                source + " | ${TMP_DIR}",
                "${LABLIE_BIN} project \"$<\" generate-runtime-makefile \"$@\""
        );

        makefile.appendMkdirRule("${TMP_DIR}");

        makefile.append("include ${TMP_DIR}/lablie.Makefile\n");

        return makefile.toString();
    }

    public String generateRuntimeMakefile(Project result) {
        MakefileBuilder makefile = new MakefileBuilder();

        makefile.appendVariable("OUT_DIR", "output");
        makefile.appendVariable("TMP_DIR", "tmp");

        makefile.append("\n");

        for(Project.DataSet dataSet : result.getDatasets()) {
            genImmediateItemsVariable(makefile,dataSet);
        }

        makefile.append("\n");


        for(Project.OutputSet outputSet : result.getOutputsets()) {
            for (String outputFormet : outputSet.getOutputMimetypes()) {
                makefile.appendVariable(
                        getOutputIdentifier(outputSet, outputFormet),
                        getOutputsGenerator(result, outputSet, outputFormet)
                );
            }
        }


        for(Project.OutputSet outputSet : result.getOutputsets()) {
            makefile.appendVariable(
                    getOutputSetIdentifier(outputSet) + "_PAPER",
                    buildPaperOptions(outputSet)
            );
        }

        makefile.append("\n");

        List<String> outputTargets = result.getOutputsets().stream()
                .flatMap(
                        (set) -> set.getOutputMimetypes().stream()
                                .map((type) -> getOutputVariable(set, type))
                )
                .collect(Collectors.toList());

        makefile.appendRule("all", String.join(" ", outputTargets));

        for(Project.OutputSet outputSet : result.getOutputsets()) {
            String svgOutputRule = outputSet.getOutputMimetypes().contains("svg")
                    ? "${OUT_DIR}/" + outputSet.getKey() + "/%.svg"
                    : "${TMP_DIR}/" + outputSet.getKey() + "/%.svg";

            generateSvgRule(makefile, result, outputSet, svgOutputRule);

            for (String outputFormat : outputSet.getOutputMimetypes()) {
                switch (outputFormat.toLowerCase()) {
                    case "svg":
                        break;
                    case "pdf":
                        makefile.appendRule(
                                "${OUT_DIR}/" + outputSet.getKey() + "/%.pdf",
                                svgOutputRule,
                                "inkscape --file=\"$<\" --without-gui --export-pdf=\"$@\""
                        );
                        break;
                    default:
                        throw new RuntimeException(String.format("Unsupported format %s", outputFormat));
                }
            }
        }

        if(result.getArchives() != null) {
            makefile.appendRule(
                    "all_archives",
                    result.getArchives().stream().map(this::getArchiveLocation)
            );

            for (Project.Archive archive : result.getArchives()) {
                if (!"zip".equals(archive.getType())) {
                    throw new RuntimeException("Only zip archives supported, yet");
                }

                makefile.appendRule(
                        getArchiveLocation(archive),
                        "all | archives",
                        "rm -f \"$@\"; zip -r \"$@\" " + String.join(" ", archive.getSources())
                );
            }
        }

        makefile.appendMkdirRule("${OUT_DIR}");
        makefile.appendMkdirRule("archives");
        for(Project.OutputSet outputSet : result.getOutputsets()) {
            makefile.appendMkdirRule("${OUT_DIR}/" + outputSet.getKey());
            makefile.appendMkdirRule("${TMP_DIR}/" + outputSet.getKey());
        }

        makefile.appendRule(
                "information",
                "",
                genInfoCommands(result)
        );

        return makefile.toString();
    }

    private void generateSvgRule(MakefileBuilder makefile, Project project, Project.OutputSet outputSet, String svgOutputRule) {
        Project.DataSet srcDataSet = getDataset(project, outputSet.getDataset());

        String srcFile;
        StringBuilder options = new StringBuilder();

        options.append("$(" + getOutputSetIdentifier(outputSet) + "_PAPER) ");

        if (srcDataSet.getJsonCollectionStorage() != null) {
            options.append("--instance-json \"$<\" ");
            srcFile = String.format("%s/%%.json", srcDataSet.getJsonCollectionStorage().getLocation());
        } else if (srcDataSet.getCsvTableStorage() != null) {
            options.append("--dataset-csv \"$<\" ");
            options.append("--instance \"$*\" ");
            srcFile = String.format("%s", srcDataSet.getCsvTableStorage().getLocation());
        } else {
            throw new RuntimeException("No storage defined");
        }

        makefile.appendRule(
                svgOutputRule,
                srcFile + " | ${OUT_DIR}/" + outputSet.getKey(),
                "$(LABLIE_BIN) tile " + options.toString() + outputSet.getTemplate() + " \"$@\""
        );
    }

    private String getArchiveLocation(Project.Archive archive) {
        return String.format("archives/%s.%s", archive.getName(), archive.getType());
    }

    private String buildPaperOptions(Project.OutputSet outputSet) {
        isTrue(outputSet.getPaper() != null, "Paper definition expected");

        TiledPaper paper = outputSet.getPaper();
        return String.format(
                "--paper-size %.3f %.3f --label-offset %.3f %.3f --label-size %.3f %.3f --label-delta %.3f %.3f",
                paper.getWith(), paper.getHeight(),
                paper.getTileOffsetX(), paper.getTileOffsetY(),
                paper.getTileWidth(), paper.getTileHeight(),
                paper.getTileDeltaX(), paper.getTileDeltaY()
        );
    }

    private String[] genInfoCommands(Project result) {
        List<String> commands = new ArrayList<>();

        result.getDatasets().stream()
                .map(dataSet -> String.format("@echo DataSet %s items: $(%s)", dataSet.getKey(), getDatasetIdentifier(dataSet)))
                .collect(Collectors.toCollection(() -> commands));

        for(Project.OutputSet outputSet : result.getOutputsets()) {
            for(String outputFormet : outputSet.getOutputMimetypes()) {
                commands.add(
                        String.format(
                                "@echo Outputs %s, %s items: $(%s)",
                                outputSet.getKey(), outputFormet, getOutputIdentifier(outputSet,outputFormet)
                        )
                );
            }
        }

        return commands.toArray(new String[commands.size()]);
    }

    private void genImmediateItemsVariable(MakefileBuilder makefile, Project.DataSet dataSet) {
        if(dataSet.getJsonCollectionStorage() != null) {
            makefile.appendVariable(
                    getDatasetIdentifier(dataSet),
                    getSourcesMatcher(dataSet.getJsonCollectionStorage())
            );
        } else if (dataSet.getCsvTableStorage() != null) {
            makefile.appendVariable(
                    getDatasetIdentifier(dataSet),
                    "$(shell csvtool namedcol key \"" + dataSet.getCsvTableStorage().getLocation() + "\" | tail -n +2 | tr '\\n' ' ')"
            );
        } else {
            throw new RuntimeException("No storage defined");
        }
    }

    private String getSourcesMatcher(Project.DataSet.JsonCollectionStorage jsonCollectionStorage) {
        return String.format("$(wildcard %s/*.json)", jsonCollectionStorage.getLocation());
    }

    private String getDatasetIdentifier(Project.DataSet dataSet) {
        return String.format("DATASET_ITEMS_%s", dataSet.getKey());
    }

    private String getOutputsGenerator(Project result, Project.OutputSet outputSet, String outputFormet) {
        Project.DataSet dataset = getDataset(result, outputSet.getDataset());

        if(dataset.getJsonCollectionStorage() != null) {
            return getOutputsGeneratorForJsonGen(dataset, outputSet, outputFormet);
        } else if (dataset.getCsvTableStorage() != null) {
            return getOutputsGeneratorForCsvGen(dataset, outputSet, outputFormet);
        } else {
            throw new RuntimeException("No storage defined");
        }
    }

    private String getOutputsGeneratorForJsonGen(Project.DataSet dataSet, Project.OutputSet outputSet, Object outputType) {
        return String.format(
                "$(patsubst %s/%%.json, ${OUT_DIR}/%s/%%.%s, ${%s})",
                dataSet.getJsonCollectionStorage().getLocation(),
                outputSet.getKey(),
                outputType,
                getDatasetIdentifier(dataSet)
        );
    }

    private String getOutputsGeneratorForCsvGen(Project.DataSet dataSet, Project.OutputSet outputSet, Object outputType) {
        return String.format(
                "$(addprefix ${OUT_DIR}/%s/, $(addsuffix .%s, ${%s}))",
                outputSet.getKey(),
                outputType,
                getDatasetIdentifier(dataSet)
        );
    }

    private Project.DataSet getDataset(Project project, String datasetKey) {
        return project.getDatasets().stream()
                .filter(dataSet -> Objects.equals(dataSet.getKey(), datasetKey))
                .findFirst().get();
    }

    private String getOutputVariable(Project.OutputSet outputSet, String mimeType) {
        return String.format("${%s}", getOutputIdentifier(outputSet, mimeType));
    }

    private String getOutputIdentifier(Project.OutputSet outputSet, String mimeType) {
        return String.format("%s_%s", getOutputSetIdentifier(outputSet), mimeType);
    }

    private String getOutputSetIdentifier(Project.OutputSet outputSet) {
        return String.format("OUTPUTS_%s", outputSet.getKey());
    }
}
