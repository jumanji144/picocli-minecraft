package me.darknet.cli.impl.bungee.converters;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import picocli.CommandLine;

public class ProxiedPlayerConverter implements CommandLine.ITypeConverter<ProxiedPlayer> {
    @Override
    public ProxiedPlayer convert(String value) throws Exception {
        ProxiedPlayer player = ProxyServer.getInstance().getPlayer(value);
        if (player == null) {
            throw new CommandLine.TypeConversionException("Player not found");
        }
        return player;
    }
}
