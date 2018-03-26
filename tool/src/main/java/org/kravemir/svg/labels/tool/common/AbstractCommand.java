package org.kravemir.svg.labels.tool.common;

import picocli.CommandLine.Command;
import picocli.CommandLine.Mixin;

@Command
public abstract class AbstractCommand implements Runnable {

    @Mixin
    private CommandHelpOption helperOptions;
}
