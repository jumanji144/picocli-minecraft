package me.darknet.cli.command;

import picocli.CommandLine;

public interface ICommandRegistrar {

    void registerCommand(CommandLine cli);

    void unregisterCommand(CommandLine cli);

    void unregisterCommand(Object command);

    CommandLine.Help.ColorScheme getColorScheme();

}
