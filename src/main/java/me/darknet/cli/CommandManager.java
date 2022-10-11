package me.darknet.cli;

import me.darknet.cli.command.Base;
import me.darknet.cli.command.ICommandRegistrar;
import me.darknet.cli.converters.Converters;
import picocli.CommandLine;

public class CommandManager {

    private final ICommandRegistrar registrar;
    private CommandLine.Help.ColorScheme colorScheme;

    public CommandManager(ICommandRegistrar registrar) {
        this.registrar = registrar;
        this.colorScheme = registrar.getColorScheme();
    }

    public void register(Base command) {
        // wrap in CommandLine
        CommandLine line = new CommandLine(command);
        Converters.registerInto(line);
        line.setColorScheme(colorScheme);
        registrar.registerCommand(line);
    }

    public void unregister(Base command) {
        registrar.unregisterCommand(command);
    }

    public void setColorScheme(CommandLine.Help.ColorScheme colorScheme) {
        this.colorScheme = colorScheme;
    }

}
