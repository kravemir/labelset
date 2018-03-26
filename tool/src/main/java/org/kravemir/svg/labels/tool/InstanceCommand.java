package org.kravemir.svg.labels.tool;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.io.FileUtils;
import org.kravemir.svg.labels.InstanceRenderer;
import org.kravemir.svg.labels.InstanceRendererImpl;
import org.kravemir.svg.labels.model.LabelTemplateDescriptor;
import org.kravemir.svg.labels.tool.common.AbstractCommand;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

import javax.xml.xpath.XPathExpressionException;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;

@Command(
        name = "instance", description = "Fill label template with instance data",
        abbreviateSynopsis = true
)
public class InstanceCommand extends AbstractCommand {

    private static final TypeReference<HashMap<String,Object>> HASH_MAP_TYPE_REFERENCE = new TypeReference<HashMap<String,Object>>() {};

    @Option(
            names = "--instance-json",
            description = "Path to JSON file containing values for single instance"
    )
    private File instanceJsonFile;

    @Parameters(
            index = "0", paramLabel = "SOURCE",
            description = "Path of a SVG file containing a label"
    )
    private File source;

    @Parameters(
            index = "1", paramLabel = "TARGET",
            description = "Path of a SVG file which should be generated"
    )
    private File target;


    public void run() {
        try {
            String svg = FileUtils.readFileToString(source);
            svg = processSVGTemplate(svg);
            FileUtils.writeStringToFile(target, svg);
        } catch (IOException | XPathExpressionException e) {
            throw new RuntimeException(e);
        }
    }

    private String processSVGTemplate(String templateOrImage) throws IOException, XPathExpressionException {
        if(instanceJsonFile == null) {
            throw new RuntimeException("JSON file null");
        }

        Path sourcePath = source.toPath();

        ObjectMapper mapper = new ObjectMapper();

        LabelTemplateDescriptor descriptor = mapper.readValue(
                FileUtils.readFileToString(sourcePath.resolveSibling(withoutExtension(sourcePath.getFileName()) + ".lablie.json").toFile()),
                LabelTemplateDescriptor.class
        );

        HashMap<String,String> values = mapper.readValue(
                FileUtils.readFileToString(instanceJsonFile),
                HASH_MAP_TYPE_REFERENCE
        );

        InstanceRenderer renderer = new InstanceRendererImpl();
        return renderer.render(templateOrImage, descriptor, values);
    }

    private String withoutExtension(Path fileName) {
        String str = fileName.toString();
        return str.substring(0, str.lastIndexOf("."));
    }
}
