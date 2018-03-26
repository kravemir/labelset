package org.kravemir.svg.labels.tooling;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.core.util.DefaultIndenter;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.apache.commons.io.FileUtils;
import org.kravemir.svg.labels.model.LabelTemplateDescriptor;
import org.kravemir.svg.labels.tool.model.ReferringLabelGroup;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashMap;

public class Loader {

    private static final TypeReference<LinkedHashMap<String, Object>> HASH_MAP_TYPE_REFERENCE = new TypeReference<LinkedHashMap<String, Object>>() {
    };

    private ObjectMapper mapper;

    public Loader() {
    }

    public LabelTemplateDescriptor loadDescriptor(File descriptorFile) throws IOException {
        return getMapper().readValue(descriptorFile, LabelTemplateDescriptor.class);
    }

    public LinkedHashMap<String, String> loadInstance(File datasetJsonFile) throws IOException {
        return getMapper().readValue(datasetJsonFile, HASH_MAP_TYPE_REFERENCE);
    }

    public void saveInstance(File selectedDatasetFile, LinkedHashMap<String, String> content) throws IOException {
        // TODO: maybe more intelligent to preserve original JSON structure and omit whitespace changes (might need better library)
        getMapper().writerWithDefaultPrettyPrinter().writeValue(selectedDatasetFile, content);
    }

    public ReferringLabelGroup.Instance[] loadInstances(File instancesJsonFile) throws IOException {
        return getMapper().readValue(
                FileUtils.readFileToString(instancesJsonFile),
                ReferringLabelGroup.Instance[].class
        );
    }

    private ObjectMapper getMapper() {
        if (mapper == null) {
            mapper = createMapper();
        }
        return mapper;
    }

    private static ObjectMapper createMapper() {
        DefaultPrettyPrinter.Indenter indenter = new DefaultIndenter("    ", DefaultIndenter.SYS_LF);
        DefaultPrettyPrinter printer = new DefaultPrettyPrinter();
        printer.indentObjectsWith(indenter);
        printer.indentArraysWith(indenter);

        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true);
        mapper.setDefaultPrettyPrinter(printer);

        org.kravemir.svg.labels.model.JacksonMixIns.registerMixIns(mapper);
        org.kravemir.svg.labels.tool.model.JacksonMixIns.registerMixIns(mapper);

        return mapper;
    }
}
