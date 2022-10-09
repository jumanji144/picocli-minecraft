package me.darknet.commandsystem.impl.bungee;

import me.darknet.commandsystem.command.AbstractCommandLoader;
import me.darknet.commandsystem.command.Arguments;
import me.darknet.commandsystem.command.Command;
import me.darknet.commandsystem.parser.ArgumentParser;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Plugin;

import java.util.UUID;

public class BungeeCommandLoader extends AbstractCommandLoader {

    private final Plugin plugin;

    public BungeeCommandLoader(Plugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean registerCommand(Command annotation, Arguments arguments, Object command) {
        BungeeCommandDelegate delegate = new BungeeCommandDelegate(command, annotation, arguments);
        ProxyServer.getInstance().getPluginManager().registerCommand(plugin, delegate);
        return true;
    }

    @Override
    public boolean unregisterCommand0(Object command) {
        ProxyServer.getInstance().getPluginManager().unregisterCommand((net.md_5.bungee.api.plugin.Command) command);
        return true;
    }

    @Override
    public void registerParsers() {
        ArgumentParser.registerParser("player", (s) -> {
            ProxiedPlayer player = ProxyServer.getInstance().getPlayer(s);
            if(player == null) throw new IllegalArgumentException("Player not found: " + s);
            return player;
        });
        ArgumentParser.registerParser("offlineplayer", (s) -> {
            // offline player is either name or uuid
            ProxiedPlayer player;
            if(s.length() == 36) {
                player = ProxyServer.getInstance().getPlayer(UUID.fromString(s));
            } else {
                player = ProxyServer.getInstance().getPlayer(s);
            }
            // non null guaranteed
            return player;
        });
        ArgumentParser.registerParser("server", (s) -> {
            // server is either name or uuid
            ServerInfo server = ProxyServer.getInstance().getServerInfo(s);
            if(server == null) throw new IllegalArgumentException("Server not found: " + s);
            return s;
        });
    }
}
