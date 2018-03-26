package org.kravemir.svg.labels.tool;

import org.kravemir.svg.labels.tool.common.CommandHelpOption;
import picocli.CommandLine;

public class ToolRunner {
    public static void main(String[] args) {
        CommandLine commandLine = constructCommandLine();

        commandLine.parseWithHandler(
                new CommandLine.RunLast().useOut(System.out).useAnsi(CommandLine.Help.Ansi.AUTO),
                args
        );
    }

    public static CommandLine constructCommandLine() {
        CommandLine commandLine = new CommandLine(new RootCommand());

        commandLine.addSubcommand("tile", new TileCommand());
        commandLine.addSubcommand("instance", new InstanceCommand());
        commandLine.addSubcommand("project", new ProjectCommand());

        commandLine.addMixin("helperOptions", new CommandHelpOption());

        commandLine.setSeparator(" ");

        return commandLine;
    }
}
