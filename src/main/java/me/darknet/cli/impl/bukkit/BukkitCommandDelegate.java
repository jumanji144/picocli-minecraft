package me.darknet.cli.impl.bukkit;

import me.darknet.cli.hack.ExecutorIntercept;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.defaults.BukkitCommand;
import picocli.CommandLine;

import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

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

    private static class SenderPrintStream extends PrintWriter {

        private final CommandSender sender;
        private static final Map<String, Character> ansiToMinecraft = new HashMap<>();

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

        static {
            ansiToMinecraft.put("\u001B[0m", 'r'); // reset
            ansiToMinecraft.put("\u001B[1m", 'l'); // bold
            ansiToMinecraft.put("\u001B[2m", 'n'); // underline
            ansiToMinecraft.put("\u001B[3m", 'o'); // italic
            ansiToMinecraft.put("\u001B[4m", 'n'); // underline
            ansiToMinecraft.put("\u001B[5m", 'n'); // underline
            ansiToMinecraft.put("\u001B[6m", 'n'); // underline
            ansiToMinecraft.put("\u001B[7m", 'n'); // underline
            ansiToMinecraft.put("\u001B[8m", 'n'); // underline
            ansiToMinecraft.put("\u001B[9m", 'n'); // underline
            ansiToMinecraft.put("\u001B[30m", '0'); // black
            ansiToMinecraft.put("\u001B[31m", '4'); // dark red
            ansiToMinecraft.put("\u001B[32m", '2'); // dark green
            ansiToMinecraft.put("\u001B[33m", '6'); // dark yellow
            ansiToMinecraft.put("\u001B[34m", '1'); // dark blue
            ansiToMinecraft.put("\u001B[35m", '5'); // dark purple
            ansiToMinecraft.put("\u001B[36m", '3'); // dark aqua
            ansiToMinecraft.put("\u001B[37m", '7'); // dark gray
            ansiToMinecraft.put("\u001B[38m", 'n'); // underline
            ansiToMinecraft.put("\u001B[39m", 'f'); // white
            ansiToMinecraft.put("\u001B[40m", 'n'); // underline
            ansiToMinecraft.put("\u001B[41m", '4'); // red
            ansiToMinecraft.put("\u001B[42m", '2'); // green
            ansiToMinecraft.put("\u001B[43m", '6'); // yellow
            ansiToMinecraft.put("\u001B[44m", '1'); // blue
            ansiToMinecraft.put("\u001B[45m", '5'); // purple
            ansiToMinecraft.put("\u001B[46m", '3'); // aqua
            ansiToMinecraft.put("\u001B[47m", '7'); // gray
        }

    }
}
