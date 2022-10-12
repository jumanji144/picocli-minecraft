package me.darknet.cli.impl.bungee;

import me.darknet.cli.command.Base;
import me.darknet.cli.hack.ExecutorIntercept;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.TabExecutor;
import picocli.AutoComplete;
import picocli.CommandLine;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

public class BungeeCommandDelegate extends Command implements TabExecutor {

    private final CommandLine cli;
    private final ExecutorIntercept intercept;
    private final BungeeBase base;

    protected BungeeCommandDelegate(CommandLine cli, ExecutorIntercept intercept) {
        super(cli.getCommandName());
        this.cli = cli;
        this.base = cli.getCommand();
        this.base.setCommandLine(cli);
        this.intercept = intercept;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {

        // setup stream
        SenderPrintStream stream = new SenderPrintStream(sender);
        cli.setErr(stream);
        cli.setOut(stream);

        // set the sender
        base.setSender(sender);
        base.setArgs(args);

        // execute the command
        intercept.prepareExecution(sender);
        cli.execute(args);

        // undo state
        intercept.finishExecution();

        // reset the sender
        base.setSender(null);
        base.setArgs(null);
    }

    @Override
    public Iterable<String> onTabComplete(CommandSender sender, String[] args) {
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

        private void send(String s) {
            sender.sendMessage(TextComponent.fromLegacyText(s));
        }

        @Override
        public void print(boolean b) {
            send(String.valueOf(b));
        }

        @Override
        public void print(char c) {
            send(String.valueOf(c));
        }

        @Override
        public void print(int i) {
            send(String.valueOf(i));
        }

        @Override
        public void print(long l) {
            send(String.valueOf(l));
        }

        @Override
        public void print(float f) {
            send(String.valueOf(f));
        }

        @Override
        public void print(double d) {
            send(String.valueOf(d));
        }

        @Override
        public void print(char[] s) {
            send(String.valueOf(s));
        }

        @Override
        public void print(String s) {
            send(s);
        }

        @Override
        public void print(Object obj) {
            send(String.valueOf(obj));
        }
    }
}
