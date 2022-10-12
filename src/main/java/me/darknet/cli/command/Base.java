package me.darknet.cli.command;

import picocli.CommandLine;

import java.util.concurrent.Callable;

/**
 * Command context class
 */
public abstract class Base implements Callable<Integer> {

    CommandLine commandLine;

    public abstract int execute();

    @Override
    public Integer call() throws Exception {
        return execute();
    }

    public CommandLine getCommandLine() {
        return commandLine;
    }

    public void setCommandLine(CommandLine commandLine) {
        this.commandLine = commandLine;
    }

    public int stub() {
        throw new CommandLine.ParameterException(getCommandLine(), "You must specify a subcommand");
    }
}
