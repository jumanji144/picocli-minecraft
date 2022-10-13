package me.darknet.cli.impl.bukkit;

import me.darknet.cli.command.ICommandRegistrar;
import me.darknet.cli.converters.Converters;
import me.darknet.cli.hack.ExecutorIntercept;
import me.darknet.cli.impl.bukkit.converters.OfflinePlayerConverter;
import me.darknet.cli.impl.bukkit.converters.PlayerConverter;
import me.darknet.cli.impl.bukkit.converters.WorldConverter;
import me.darknet.cli.util.Reflection;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.command.CommandMap;
import org.bukkit.entity.Player;
import org.bukkit.permissions.Permissible;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.SimplePluginManager;
import picocli.CommandLine;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class BukkitCommandRegistrar implements ICommandRegistrar {

    private static final Object commandMapObj;
    private static Object pureMapObject;
    private static final Method registerCommand;
    private static final Method unregisterCommand;
    private final Plugin plugin;

    public BukkitCommandRegistrar(Plugin plugin) {
        this.plugin = plugin;
    }

    private final Map<String, BukkitCommandDelegate> commandMap = new HashMap<>();
    private final Map<Object, BukkitCommandDelegate> baseToDelegate = new HashMap<>();

    @Override
    public void registerCommand(CommandLine cli) {
        cli.setExecutionStrategy(new ExecutorIntercept(BukkitCommandRegistrar::permissionCheck));
        BukkitCommandDelegate delegate = new BukkitCommandDelegate(cli);
        // register delegate
        commandMap.put(cli.getCommandName(), delegate);
        baseToDelegate.put(cli.getCommand(), delegate);
        try {
            registerCommand.invoke(commandMapObj, plugin.getName(), delegate);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void unregisterCommand(CommandLine cli) {
        try {
            unregisterCommand.invoke(pureMapObject, commandMap.get(cli.getCommandName()));
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void unregisterCommand(Object command) {
        try {
            unregisterCommand.invoke(pureMapObject, baseToDelegate.get(command));
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public CommandLine.Help.ColorScheme getColorScheme() {
        Map<String, CommandLine.Help.Ansi.IStyle> markupMap = new HashMap<>();
        markupMap.put("reset", BukkitStyle.reset);
        return new CommandLine.Help.ColorScheme.Builder(CommandLine.Help.Ansi.ON)
                .commands(BukkitStyle.white)
                .options(BukkitStyle.yellow)
                .parameters(BukkitStyle.green)
                .errors(BukkitStyle.red)
                .stackTraces(BukkitStyle.red)
                .optionParams(BukkitStyle.green)
                .customMarkupMap(markupMap)
                .build();
    }

    private static boolean permissionCheck(Object caller, String permission) {
        if(caller instanceof Permissible) {
            return ((Permissible) caller).hasPermission(permission);
        } else {
            throw new IllegalArgumentException("Illegal caller, caller == " + caller);
        }
    }
    
    static {
        // hack to get the command map
        PluginManager manager = Bukkit.getPluginManager();
        if(manager instanceof SimplePluginManager) {
            try {
                commandMapObj = Reflection.fieldGet(manager, "commandMap");
                try {
                    pureMapObject = Reflection.fieldGet(commandMapObj, "knownCommands");
                } catch (NoSuchFieldException e) {
                    // knownCommands field is hidden in 1.13+, but we can still get it
                    try {
                        pureMapObject = Reflection.invoke(commandMapObj, "getKnownCommands()Ljava/util/Map;");
                    } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException ex) {
                        throw new RuntimeException("Unsupported command map, map == " + commandMapObj, ex);
                    }
                }
                registerCommand = Reflection.lookupMethod(CommandMap.class,
                        "register(Ljava/lang/String;Lorg/bukkit/command/Command;)Z");
                // no unregister method, just point to the map `remove` method
                unregisterCommand = Reflection.lookupMethod(Map.class,
                        "remove(Ljava/lang/Object;)Ljava/lang/Object;");
            } catch (NoSuchFieldException | IllegalAccessException | ClassNotFoundException | NoSuchMethodException e) {
                throw new RuntimeException("Unsupported command map, map == " + manager, e);
            }
        } else {
            throw new RuntimeException("Unsupported plugin manager, manager == " + manager);
        }
        // register converters
        Converters.register(World.class, new WorldConverter());
        Converters.register(Player.class, new PlayerConverter());
        Converters.register(OfflinePlayer.class, new OfflinePlayerConverter());
    }

}
