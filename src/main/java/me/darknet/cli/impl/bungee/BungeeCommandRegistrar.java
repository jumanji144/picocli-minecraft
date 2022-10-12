package me.darknet.cli.impl.bungee;

import me.darknet.cli.command.ICommandRegistrar;
import me.darknet.cli.converters.Converters;
import me.darknet.cli.hack.ExecutorIntercept;
import me.darknet.cli.impl.bukkit.BukkitStyle;
import me.darknet.cli.impl.bungee.converters.ProxiedPlayerConverter;
import me.darknet.cli.impl.bungee.converters.ServerInfoConverter;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Plugin;
import picocli.CommandLine;

import java.util.HashMap;
import java.util.Map;

public class BungeeCommandRegistrar implements ICommandRegistrar {

    private final Plugin plugin;

    public BungeeCommandRegistrar(Plugin plugin) {
        this.plugin = plugin;
    }

    Map<Object, BungeeCommandDelegate> commands;

    @Override
    public void registerCommand(CommandLine cli) {
        BungeeCommandDelegate delegate = new BungeeCommandDelegate(cli, new ExecutorIntercept((BungeeCommandRegistrar::checkPermission)));
        ProxyServer.getInstance().getPluginManager().registerCommand(this.plugin, delegate);
        commands.put(cli.getCommand(), delegate);
    }

    @Override
    public void unregisterCommand(CommandLine cli) {
        this.unregisterCommand((Object) cli.getCommand());
    }

    @Override
    public void unregisterCommand(Object command) {
        ProxyServer.getInstance().getPluginManager().unregisterCommand(commands.get(command));
    }

    @Override
    public CommandLine.Help.ColorScheme getColorScheme() {
        Map<String, CommandLine.Help.Ansi.IStyle> markupMap = new HashMap<>();
        markupMap.put("reset", BungeeStyle.reset);
        return new CommandLine.Help.ColorScheme.Builder(CommandLine.Help.Ansi.ON)
                .commands(BungeeStyle.white)
                .options(BungeeStyle.yellow)
                .parameters(BungeeStyle.green)
                .errors(BungeeStyle.red)
                .stackTraces(BungeeStyle.red)
                .optionParams(BungeeStyle.green)
                .customMarkupMap(markupMap)
                .build();
    }

    private static boolean checkPermission(Object sender, String permission) {
        return ((CommandSender) sender).hasPermission(permission);
    }

    static {
        // register converters
        Converters.register(ProxiedPlayer.class, new ProxiedPlayerConverter());
        Converters.register(ServerInfo.class, new ServerInfoConverter());
    }
}
