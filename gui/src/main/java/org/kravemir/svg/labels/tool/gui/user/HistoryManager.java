package org.kravemir.svg.labels.tool.gui.user;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.kravemir.svg.labels.tool.model.HistoryProjectRecord;

import java.io.File;
import java.io.StringWriter;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

public class HistoryManager {

    private static final String USER_HOME = System.getProperty("user.home");

    public static Collection<HistoryProjectRecord> loadConfig() {
        try {
            File configFile = getConfigFile();
            ObjectMapper mapper = getMapper();


            HistoryProjectRecord[] records = mapper.readValue(
                    FileUtils.readFileToString(configFile),
                    HistoryProjectRecord[].class
            );

            return Arrays.stream(records).collect(Collectors.toList());
        } catch (Exception e) {
            e.printStackTrace(System.err);
            return Collections.emptyList();
        }
    }

    public static Collection<HistoryProjectRecord> saveNewRecord(HistoryProjectRecord newRecord) {
        try {
            File configFile = getConfigFile();
            ObjectMapper mapper = getMapper();

            Collection<HistoryProjectRecord> existingRecords = loadConfig();
            existingRecords.removeIf(record -> newRecord.getPath().equals(record.getPath()));
            existingRecords.removeIf(record -> StringUtils.isEmpty(record.getPath()));

            List<HistoryProjectRecord> records = new ArrayList<>();
            records.add(newRecord);
            records.addAll(existingRecords);

            StringWriter writer = new StringWriter();
            mapper.writeValue(writer, records.toArray(new HistoryProjectRecord[0]));

            FileUtils.writeStringToFile(configFile, writer.toString());
            return records;
        } catch (Exception e) {
            e.printStackTrace(System.err);
            return Collections.emptyList();
        }
    }

    private static ObjectMapper getMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true);

        org.kravemir.svg.labels.model.JacksonMixIns.registerMixIns(mapper);
        org.kravemir.svg.labels.tool.model.JacksonMixIns.registerMixIns(mapper);

        return mapper;
    }

    private static File getConfigFile() {
        return Paths.get(USER_HOME, ".config", "svg-labels", "projects_history").toFile();
    }
}
