package me.darknet.commandsystem.impl.bukkit;

import me.darknet.commandsystem.command.Arguments;
import me.darknet.commandsystem.command.Command;
import me.darknet.commandsystem.command.AbstractCommandLoader;
import me.darknet.commandsystem.parser.ArgumentParser;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.UUID;

public class BukkitCommandLoader extends AbstractCommandLoader {

    private static final Method addCommand;
    private static final Object commandMap;
    private final Plugin plugin;

    public BukkitCommandLoader(Plugin plugin) {
        super();
        this.plugin = plugin;
    }

    @Override
    public boolean registerCommand(Command annotation, Arguments arguments, Object command) {
        BukkitCommandDelegate delegate = new BukkitCommandDelegate(command, annotation, arguments);
        try {
            addCommand.invoke(commandMap, plugin.getName(), delegate);
            return true;
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean unregisterCommand0(Object command) {
        try {
            Field field = org.bukkit.command.Command.class.getDeclaredField("name");
            field.setAccessible(true);
            Object bukkitCommand = field.get(command);
            field = commandMap.getClass().getDeclaredField("knownCommands");
            field.setAccessible(true);
            Object map = field.get(commandMap);
            field.setAccessible(true);
            Method remove = map.getClass().getDeclaredMethod("remove", Object.class);
            remove.setAccessible(true);
            remove.invoke(map, bukkitCommand);
            return true;
        } catch (NoSuchFieldException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public void registerParsers() {
        ArgumentParser.registerParser("player", (s) -> {
           Player player = Bukkit.getPlayer(s);
           if(player == null) throw new IllegalArgumentException("Player not found: " + s);
           return player;
        });
        ArgumentParser.registerParser("offlineplayer", (s) -> {
            // offline player is either name or uuid
            OfflinePlayer player;
            if(s.length() == 36) {
                player = Bukkit.getOfflinePlayer(UUID.fromString(s));
            } else {
                player = Bukkit.getOfflinePlayer(s);
            }
            // non null guaranteed
            return player;
        });
        ArgumentParser.registerParser("world", (s) -> {
            // world is either name or uuid
            if(s.length() == 36) {
                return Bukkit.getWorld(UUID.fromString(s));
            } else {
                return Bukkit.getWorld(s);
            }
        });
        ArgumentParser.registerParser("location", (s) -> {
            String[] split = s.split(",");
            if(split.length == 4) {
                return new Location(
                        Bukkit.getWorld(UUID.fromString(split[0])),
                        Double.parseDouble(split[1]),
                        Double.parseDouble(split[2]),
                        Double.parseDouble(split[3])
                );
            } else if (split.length == 6) {
                return new Location(
                        Bukkit.getWorld(UUID.fromString(split[0])),
                        Double.parseDouble(split[1]),
                        Double.parseDouble(split[2]),
                        Double.parseDouble(split[3]),
                        Float.parseFloat(split[4]),
                        Float.parseFloat(split[5])
                );
            } else if(split.length == 3) {
                return new Location(
                        null,
                        Double.parseDouble(split[0]),
                        Double.parseDouble(split[1]),
                        Double.parseDouble(split[2])
                );
            } else {
                throw new IllegalArgumentException("Invalid location format: " + s);
            }
        });
    }

    static {
        try {
            Class<?> simplePluginManager = Class.forName("org.bukkit.plugin.SimplePluginManager");
            Field commandMapField = simplePluginManager.getDeclaredField("commandMap");
            commandMapField.setAccessible(true);
            commandMap = commandMapField.get(Bukkit.getPluginManager());
            addCommand = commandMap.getClass().getDeclaredMethod("register", String.class, org.bukkit.command.Command.class);
        } catch (ClassNotFoundException | NoSuchFieldException | IllegalAccessException | NoSuchMethodException e) {
            throw new RuntimeException(e);
        }
    }
}
