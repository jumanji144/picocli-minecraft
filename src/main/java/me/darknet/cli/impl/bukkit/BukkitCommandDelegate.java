package me.darknet.cli.impl.bukkit;

import me.darknet.cli.hack.ExecutorIntercept;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.defaults.BukkitCommand;
import picocli.AutoComplete;
import picocli.CommandLine;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class BukkitCommandDelegate extends BukkitCommand {

    private final CommandLine cli;
    private final ExecutorIntercept executor;

    protected BukkitCommandDelegate(CommandLine cli) {
        super(cli.getCommandSpec().name(), "", "", Arrays.asList(cli.getCommandSpec().aliases()));
        this.cli = cli;
        BukkitBase base = (BukkitBase) cli.getCommandSpec().userObject();
        base.setCommandLine(cli);
        this.executor = (ExecutorIntercept) cli.getExecutionStrategy(); // guaranteed to be an instance of ExecutorIntercept
    }

    @Override
    public boolean execute(CommandSender sender, String commandLabel, String[] args) {
        SenderPrintStream stream = new SenderPrintStream(sender);

        cli.setErr(stream);
        cli.setOut(stream);
        cli.setExecutionExceptionHandler((ex, commandLine, parseResult) -> {
            if(ex instanceof CommandLine.TypeConversionException) {
                sender.sendMessage(ChatColor.RED + ex.getMessage());
            } else {
                throw ex;
            }
            return 0;
        });

        // this is very hacky, only works if command execution is synchronous
        BukkitBase base = cli.getCommand(); // hack
        base.setSender(sender); // hack
        base.setArgs(args); // hack
        executor.prepareExecution(sender); // hack
        int ret = cli.execute(args);
        executor.finishExecution(); // hack
        base.setSender(null); // hack
        base.setArgs(null); // hack

        return ret == 0;
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String alias, String[] args) throws IllegalArgumentException {
        List<CharSequence> completions = new ArrayList<>();
        String lastArg = args.length > 0 ? args[args.length - 1] : "";
        int argsLength = args.length == 0 ? 0 : args.length - 1;
        AutoComplete.complete(cli.getCommandSpec(), args, argsLength, lastArg.length(), lastArg.length(), completions);
        List<String> ret = new ArrayList<>();
        for (CharSequence completion : completions) {
            ret.add(lastArg + completion.toString());
        }
        return ret;
    }

    private static class SenderPrintStream extends PrintWriter {

        private final CommandSender sender;

        public SenderPrintStream(CommandSender sender) {
            super(System.out);
            this.sender = sender;
        }

        @Override
        public void print(boolean b) {
            sender.sendMessage(String.valueOf(b));
        }

        @Override
        public void print(char c) {
            sender.sendMessage(String.valueOf(c));
        }

        @Override
        public void print(int i) {
            sender.sendMessage(String.valueOf(i));
        }

        @Override
        public void print(long l) {
            sender.sendMessage(String.valueOf(l));
        }

        @Override
        public void print(float f) {
            sender.sendMessage(String.valueOf(f));
        }

        @Override
        public void print(double d) {
            sender.sendMessage(String.valueOf(d));
        }

        @Override
        public void print(char[] s) {
            sender.sendMessage(String.valueOf(s));
        }

        @Override
        public void print(String s) {
            sender.sendMessage(s);
        }

        @Override
        public void print(Object obj) {
            sender.sendMessage(String.valueOf(obj));
        }

    }
}
