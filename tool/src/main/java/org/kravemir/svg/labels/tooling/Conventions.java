package org.kravemir.svg.labels.tooling;

import java.io.File;
import java.nio.file.Path;

public class Conventions {

    public File resolveDescriptorFileForTemplate(File templateFile) {
        Path templatePath = templateFile.toPath();
        return templatePath.resolveSibling(withoutExtension(templatePath.getFileName()) + ".lablie.json").toFile();
    }

    private String withoutExtension(Path fileName) {
        String str = fileName.toString();
        return str.substring(0, str.lastIndexOf("."));
    }
}
