package me.darknet.cli.impl.bungee.converters;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;
import picocli.CommandLine;

public class ServerInfoConverter implements CommandLine.ITypeConverter<ServerInfo> {
    @Override
    public ServerInfo convert(String value) throws Exception {
        ServerInfo server = ProxyServer.getInstance().getServerInfo(value);
        if (server == null) {
            throw new CommandLine.TypeConversionException("Server not found");
        }
        return server;
    }
}
