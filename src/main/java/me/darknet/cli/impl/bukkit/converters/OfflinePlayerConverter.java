package me.darknet.cli.impl.bukkit.converters;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import picocli.CommandLine;

public class OfflinePlayerConverter implements CommandLine.ITypeConverter<OfflinePlayer> {
    @Override
    public OfflinePlayer convert(String value) throws Exception {
        // argument can either be uuid or name
        // offline players cannot be null
        if(value.length() == 36) {
            return Bukkit.getOfflinePlayer(java.util.UUID.fromString(value));
        } else {
            return Bukkit.getOfflinePlayer(value);
        }
    }
}
