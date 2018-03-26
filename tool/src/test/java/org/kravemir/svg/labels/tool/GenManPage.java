package org.kravemir.svg.labels.tool;

import picocli.CommandLine;

import java.io.PrintStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class GenManPage {

    public static void main(String[] args) {
        CommandLine commandLine = ToolRunner.constructCommandLine();

        generateManPage(System.out, commandLine.getCommandSpec());
    }

    private static void generateManPage(PrintStream out, CommandLine.Model.CommandSpec commandSpec) {
        printTitle(out, commandSpec);
        printName(out, commandSpec);
        printSynopsis(out, commandSpec);
        printDescription(out, commandSpec);
        printOptions(out, commandSpec);
    }

    private static void printTitle(PrintStream out, CommandLine.Model.CommandSpec commandSpec) {
        out.println(String.format(
                ".TH %s %d \"%s\"",
                commandSpec.name().toUpperCase(),
                1,
                DateTimeFormatter.ofPattern("dd MMMM YYYY", Locale.ENGLISH).format(LocalDate.now())
        ));
    }

    private static void printName(PrintStream out, CommandLine.Model.CommandSpec commandSpec) {
        out.println(".SH NAME");
        out.print(commandSpec.name());

        if(commandSpec.usageMessage().description().length > 0) {
            out.print(" \\- ");
            out.print(commandSpec.usageMessage().description()[0]);
        }

        out.println();
    }

    private static void printSynopsis(PrintStream out, CommandLine.Model.CommandSpec commandSpec) {
        out.println(".SH SYNOPSIS");

        printSynopsisAll(out, commandSpec);
    }

    private static void printSynopsisAll(PrintStream out, CommandLine.Model.CommandSpec commandSpec) {
        CommandLine.Help help = new CommandLine.Help(commandSpec);
        out.println(help.detailedSynopsis(0,null, false).trim());

        for(CommandLine subCommand : commandSpec.subcommands().values()) {
            if(subCommand.getCommandSpec().usageMessage().hidden()) {
                continue;
            }

            out.println();
            printSynopsisAll(out, subCommand.getCommandSpec());
        }
    }

    private static void printDescription(PrintStream out, CommandLine.Model.CommandSpec commandSpec) {
        out.println(".SH DESCRIPTION");
        out.println("This manual page documents briefly the");
        out.println(".B " + commandSpec.name());
        out.println("command.");
    }

    private static void printOptions(PrintStream out, CommandLine.Model.CommandSpec commandSpec) {
        out.println(".SH OPTIONS");
        printOptionsFor(out, "", commandSpec);
    }

    private static void printOptionsFor(PrintStream out, String start, CommandLine.Model.CommandSpec commandSpec) {
        String name = start + commandSpec.name();

        out.println("Summary of options for ");
        out.println(".B " + name);
        out.println("command:");
        for(CommandLine.Model.OptionSpec option : commandSpec.options()) {
            out.println(".TP");
            out.println(".B " + escape(option.names()[0]));
            out.println(String.join("\n", option.description()));
        }
        out.println(".LP");

        for(CommandLine subCommand : commandSpec.subcommands().values()) {
            if(subCommand.getCommandSpec().usageMessage().hidden()) {
                continue;
            }

            out.println();
            out.println(".SS " + name + " " + subCommand.getCommandName() + ":");
            out.println();
            printOptionsFor(out, name + " ", subCommand.getCommandSpec());
        }
    }

    private static String escape(String s) {
        return s.replace("-", "\\-");
    }
}
