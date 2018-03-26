package org.kravemir.svg.labels.tool;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.io.FileUtils;
import org.kravemir.svg.labels.tool.common.CommandHelpOption;
import org.kravemir.svg.labels.tool.model.Project;
import org.kravemir.svg.labels.tool.project.ProjectMakefileGenerator;
import picocli.CommandLine.Command;
import picocli.CommandLine.Mixin;
import picocli.CommandLine.Parameters;

import java.io.File;
import java.io.IOException;

@Command(
        description = "Group command for project manipulation sub-commands",
        abbreviateSynopsis = true
)
public class ProjectCommand implements Runnable {

    @Mixin
    private CommandHelpOption helperOptions;

    @Parameters(
            paramLabel = "PROJECT_FILE", arity = "0..1",
            description = "File containing project configuration"
    )
    private File projectFile;


    private final ObjectMapper mapper;

    public ProjectCommand() {
        mapper = new ObjectMapper();
        mapper.configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true);

        org.kravemir.svg.labels.model.JacksonMixIns.registerMixIns(mapper);
        org.kravemir.svg.labels.tool.model.JacksonMixIns.registerMixIns(mapper);
    }


    public File getProjectFile() {
        return projectFile;
    }

    @Override
    public void run() {
        System.err.println("The command project is not runnable by itself, see --help");
        System.exit(1);
    }

    @Command(
            name = "generate-makefile",
            description = "Generates makefile for project",
            abbreviateSynopsis = true,
            mixinStandardHelpOptions = true
    )
    public void commandGenerateMakefile() {
        try {
            String makefile = new ProjectMakefileGenerator().generateMakefile(getProjectFile().getPath());
            FileUtils.writeStringToFile(new File("Makefile"), makefile);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    @Command(
            name = "generate-runtime-makefile",
            description = "Generates runtime makefile for project",
            abbreviateSynopsis = true,
            mixinStandardHelpOptions = true,
            hidden = true
    )
    public void commandGenerateRuntimeMakefile(
            @Parameters(paramLabel = "OUTPUT_FILE", arity = "0..1") File outputFile
    ) {
        try {
            Project project = mapper.readValue(getProjectFile(), Project.class);
            String makefile = new ProjectMakefileGenerator().generateRuntimeMakefile(project);
            FileUtils.writeStringToFile(outputFile, makefile);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
