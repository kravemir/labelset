package org.kravemir.svg.labels.tool.project;

import java.util.stream.Collectors;
import java.util.stream.Stream;

public class MakefileBuilder {

    private StringBuilder target;

    public MakefileBuilder() {
        this.target = new StringBuilder();
    }

    public void appendVariable(String name, String value) {
        appendVariable(name, value, false);
    }

    public void appendVariable(String name, String value, boolean export) {
        target.append(name);
        target.append(" := ");
        target.append(value);
        target.append("\n");

        if(export) {
            target.append("export ");
            target.append(name);
            target.append("\n");
        }
    }

    public void appendRule(String output, Stream<String> depends, String... commands) {
        appendRule(
                output,
                depends.collect(Collectors.joining(" ")),
                commands
        );
    }

    public void appendRule(String output, String depends, String... commands) {
        target.append(output);
        target.append(": ");
        target.append(depends);

        for(String cmd : commands) {
            target.append("\n\t");
            target.append(cmd);
        }

        target.append("\n\n");
    }

    public void appendMkdirRule(String dirname) {
        target.append(dirname);
        target.append(": ;\n\tmkdir -p $@\n\n");
    }

    public void append(CharSequence csq) {
        target.append(csq);
    }

    @Override
    public String toString() {
        return target.toString();
    }
}
