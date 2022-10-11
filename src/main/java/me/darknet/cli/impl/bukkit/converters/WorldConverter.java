package me.darknet.cli.impl.bukkit.converters;

import org.bukkit.World;
import picocli.CommandLine;

public class WorldConverter implements CommandLine.ITypeConverter<World> {
    @Override
    public World convert(String value) throws Exception {
        World world = org.bukkit.Bukkit.getWorld(value);
        if (world == null) {
            throw new CommandLine.TypeConversionException("World not found");
        }
        return world;
    }
}
