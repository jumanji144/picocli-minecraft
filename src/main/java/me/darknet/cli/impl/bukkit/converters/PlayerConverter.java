package me.darknet.cli.impl.bukkit.converters;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import picocli.CommandLine;

public class PlayerConverter implements CommandLine.ITypeConverter<Player> {
    @Override
    public Player convert(String value) throws Exception {
        Player player = Bukkit.getPlayer(value);
        if (player == null) {
            throw new CommandLine.TypeConversionException("Player not found");
        }
        return player;
    }
}
