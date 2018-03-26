package org.kravemir.svg.labels.tool;

import com.fasterxml.jackson.core.type.TypeReference;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.io.FileUtils;
import org.kravemir.svg.labels.TileRenderer;
import org.kravemir.svg.labels.TileRendererImpl;
import org.kravemir.svg.labels.model.DocumentRenderOptions;
import org.kravemir.svg.labels.model.LabelGroup;
import org.kravemir.svg.labels.model.LabelTemplateDescriptor;
import org.kravemir.svg.labels.model.TiledPaper;
import org.kravemir.svg.labels.tool.common.AbstractCommand;
import org.kravemir.svg.labels.tool.common.PaperOptions;
import org.kravemir.svg.labels.tool.model.ReferringLabelGroup;
import org.kravemir.svg.labels.tooling.Conventions;
import org.kravemir.svg.labels.tooling.Loader;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Mixin;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

import java.io.*;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;

import static org.apache.commons.lang3.Validate.isTrue;

@Command(
        name = "tile", description = "Tile labels",
        abbreviateSynopsis = true
)
public class TileCommand extends AbstractCommand {

    private static class CSVFormatConverter implements CommandLine.ITypeConverter<CSVFormat> {
        @Override
        public CSVFormat convert(String value) throws Exception {
            return CSVFormat.valueOf(value);
        }
    }

    private static final TypeReference<HashMap<String, Object>> HASH_MAP_TYPE_REFERENCE = new TypeReference<HashMap<String, Object>>() {
    };

    @Mixin
    private PaperOptions paperOptions;

    @Option(
            names = "--instance-json", paramLabel = "FILE",
            description = "Path to JSON file containing values for single instance"
    )
    private File instanceJsonFile;

    @Option(
            names = "--instances-json", paramLabel = "FILE",
            description = "Path to JSON file containing array of instances (can be used in combination with --dataset-json)"
    )
    private File instancesJsonFile;

    @Option(
            names = "--dataset-json", paramLabel = "FOLDER",
            description = "Path to folder containing JSON files for instances"
    )
    private Path datasetJsonPath;

    @Option(
            names = "--dataset-csv", paramLabel = "FILE",
            description = "Path to CSV file containing instances"
    )
    private Path datasetCSVPath;

    @Option(
            names = "--dataset-csv-format", paramLabel = "FORMAT",
            converter = CSVFormatConverter.class, defaultValue = "Default",
            description = "Sets format for parsing CSV dataset (available options: Default, Excel, InformixUnload, InformixUnloadCsv, MySQL, PostgreSQLCsv, PostgreSQLText, RFC4180, TDF)"
    )
    private CSVFormat datasetCSVFormat;

    @Option(
            names = "--instance", paramLabel = "KEY",
            description = "Key of instance to be rendered"
    )
    private String instance;

    @Option(
            names = "--template-descriptor", paramLabel = "FILE",
            description = "Path to JSON file containing descriptor of template"
    )
    private File templateDescriptorFile;

    @Parameters(
            index = "0", paramLabel = "SOURCE",
            description = "Path to SVG file containing a label"
    )
    private File source;

    @Parameters(
            index = "1", paramLabel = "TARGET",
            description = "Path to SVG file which should be generated"
    )
    private File target;


    private final TileRenderer renderer;
    private final Loader loader;
    private final Conventions conventions;

    public TileCommand() {
        renderer = new TileRendererImpl();
        loader = new Loader();
        conventions = new Conventions();
    }


    public void run() {
        try {
            TiledPaper paper = paperOptions.buildPaper();
            List<LabelGroup> labelGroups = loadData();
            DocumentRenderOptions renderOptions = DocumentRenderOptions.newBuilder().build();

            List<String> result = renderer.render(paper, labelGroups, renderOptions);

            save(result);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private List<LabelGroup> loadData() throws IOException {
        String svg = FileUtils.readFileToString(source);

        if (instancesJsonFile != null) {
            return loadInstances(svg);
        } else if (instanceJsonFile != null) {
            return loadInstance(svg);
        } else if (datasetCSVPath != null) {
            return loadInstanceFromCSV(svg);
        } else {
            return Collections.singletonList(LabelGroup.newBuilder()
                    .setTemplate(svg)
                    .addAllInstances(
                            Collections.singletonList(LabelGroup.Instance.newBuilder().setFillPage(true).build())
                    )
                    .build());
        }
    }

    private void save(List<String> result) throws IOException {
        if(result.size() == 1) {
            FileUtils.writeStringToFile(target, result.get(0));
        } else {
            String path = target.getPath();
            int lastDot = path.lastIndexOf('.');
            String base = path.substring(0,lastDot);
            String extension = path.substring(lastDot);

            for(int i = 0; i < result.size(); i++) {
                FileUtils.writeStringToFile(
                        new File(base + "." + i + extension),
                        result.get(i)
                );
            }
        }
    }

    private List<LabelGroup> loadInstance(String templateOrImage) throws IOException {
        LabelTemplateDescriptor descriptor = loader.loadDescriptor(requireDescriptorFile());
        HashMap<String, String> values = loader.loadInstance(instanceJsonFile);

        return Collections.singletonList(
                LabelGroup.newBuilder()
                        .setTemplate(templateOrImage)
                        .setTemplateDescriptor(descriptor)
                        .addInstance(LabelGroup.Instance.newBuilder().setFillPage(true).setInstanceContent(values).build())
                        .build()
        );
    }

    private File getDescriptorFile() {
        if (templateDescriptorFile != null) {
            return templateDescriptorFile;
        } else {
            return conventions.resolveDescriptorFileForTemplate(source);
        }
    }

    private File requireDescriptorFile() {
        File descriptorFile = getDescriptorFile();

        if(descriptorFile.exists() == false) {
            System.err.println("Template descriptor file " + descriptorFile + " doesn't exist.");
            System.exit(1);
        }

        return descriptorFile;
    }

    private List<LabelGroup> loadInstances(String templateOrImage) throws IOException {
        LabelTemplateDescriptor descriptor = loader.loadDescriptor(requireDescriptorFile());
        ReferringLabelGroup.Instance[] instances = loader.loadInstances(instancesJsonFile);

        return Collections.singletonList(
                LabelGroup.newBuilder()
                        .setTemplate(templateOrImage)
                        .setTemplateDescriptor(descriptor)
                        .addAllInstances(
                                Arrays.stream(instances)
                                        .map(this::mapInstance)
                                        .collect(Collectors.toList())
                        )
                        .build()
        );
    }

    private List<LabelGroup> loadInstanceFromCSV(String templateOrImage) throws IOException {
        Reader reader = new BufferedReader(new FileReader(datasetCSVPath.toFile()));
        CSVParser parser = new CSVParser(reader, datasetCSVFormat.withHeader().withIgnoreEmptyLines().withTrim());

        Map<String, Map<String, String>> instances = loadInstances(parser);
        Map<String, String> instanceData = instances.get(instance);

        LabelTemplateDescriptor descriptor = loader.loadDescriptor(requireDescriptorFile());

        return Collections.singletonList(
                LabelGroup.newBuilder()
                        .setTemplate(templateOrImage)
                        .setTemplateDescriptor(descriptor)
                        .addInstance(LabelGroup.Instance.newBuilder().setFillPage(true).setInstanceContent(instanceData).build())
                        .build()
        );
    }

    private Map<String, Map<String, String>> loadInstances(final CSVParser parser) throws IOException {
        isTrue(parser.getHeaderMap().containsKey("key"), "CSV must contain 'key' column");
        final int headerColumn = parser.getHeaderMap().get("key");

        return parser.getRecords().stream().collect(Collectors.toMap(
                r -> r.get(headerColumn),
                CSVRecord::toMap
        ));
    }

    private LabelGroup.Instance mapInstance(ReferringLabelGroup.Instance instance) {
        LabelGroup.Instance.Builder builder = LabelGroup.Instance.newBuilder();

        if (instance.getInstanceContent() != null && !isEmpty(instance.getInstanceContentRef())) {
            // TODO: cleanup / think about this, override?
            throw new RuntimeException("Both ref and content are present");
        } else if (instance.getInstanceContent() != null) {
            builder.setInstanceContent(instance.getInstanceContent());
        } else if (!"".equals(instance.getInstanceContentRef())) {
            builder.setInstanceContent(loadInstanceContent(instance.getInstanceContentRef()));
        } else {
            // TODO: cleanup / think about this, not content
            throw new RuntimeException("None of ref and content are present");
        }

        if (instance.getFillPage()) {
            builder.setFillPage(true);
        } else {
            builder.setCount(instance.getCount());
        }

        return builder.build();
    }

    private boolean isEmpty(String value) {
        return value == null || "".equals(value);
    }

    private Map<String, String> loadInstanceContent(String name) {

        try {
            return loader.loadInstance(datasetJsonPath.resolve(name + ".json").toFile());
        } catch (IOException e) {
            // TODO: error handling
            throw new RuntimeException(e);
        }
    }
}
