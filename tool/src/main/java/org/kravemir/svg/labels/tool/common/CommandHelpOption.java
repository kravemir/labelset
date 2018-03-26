package org.kravemir.svg.labels.tool.common;

import picocli.CommandLine.Option;

public class CommandHelpOption {

    @Option(
            names = { "-h", "--help" }, usageHelp = true,
            description = "display a help message"
    )
    private boolean helpRequested = false;
}
