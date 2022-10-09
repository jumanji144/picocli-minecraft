package me.darknet.commandsystem;

import me.darknet.commandsystem.command.CommandLoader;

public class CommandManager {

    private final CommandLoader loader;

    public CommandManager(CommandLoader loader) {
        this.loader = loader;
    }

    public boolean registerCommand(Object command) {
        return this.loader.registerCommand(command);
    }

    public boolean unregisterCommand(Object command) {
        return this.loader.unregisterCommand(command);
    }

}
