package me.darknet.commandsystem.command;

import org.bukkit.command.CommandSender;

import java.util.Map;

/**
 * Class that holds information for the execution of a command
 * Which is passed to the command delegate
 */
public class CommandContext {

    private final Map<String, Object> parsedArguments;
    private final String[] pureArguments;
    private final String label;

    public CommandContext(Map<String, Object> parsedArguments, String[] pureArguments, String label) {
        this.parsedArguments = parsedArguments;
        this.pureArguments = pureArguments;
        this.label = label;
    }

    public <T> T getArgument(String name) {
        return (T) this.parsedArguments.get(name);
    }

    public String[] getArgs() {
        return this.pureArguments;
    }

    public String getLabel() {
        return this.label;
    }

}
