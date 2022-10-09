package me.darknet.commandsystem.command;

public interface CommandLoader {

    boolean registerCommand(Object command);

    boolean unregisterCommand(Object command);

    void unregisterAll();
}
