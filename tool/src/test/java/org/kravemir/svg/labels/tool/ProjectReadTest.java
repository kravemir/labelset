package org.kravemir.svg.labels.tool;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import org.kravemir.svg.labels.tool.model.Project;

import java.io.IOException;

public class ProjectReadTest {

    @Test
    public void test() throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        org.kravemir.svg.labels.model.JacksonMixIns.registerMixIns(mapper);
        org.kravemir.svg.labels.tool.model.JacksonMixIns.registerMixIns(mapper);

        Project project = mapper.readValue(
                getClass().getResource("/test-project.lablie-project.json"),
                Project.class
        );
        System.out.println(project.toString());
    }
}
