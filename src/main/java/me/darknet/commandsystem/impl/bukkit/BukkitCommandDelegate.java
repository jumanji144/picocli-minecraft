package me.darknet.commandsystem.impl.bukkit;

import me.darknet.commandsystem.command.Argument;
import me.darknet.commandsystem.command.Command;
import me.darknet.commandsystem.command.Arguments;
import me.darknet.commandsystem.parser.ArgumentParser;
import org.bukkit.command.CommandSender;
import org.bukkit.command.defaults.BukkitCommand;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Map;

/**
 * Simple interface to delegate command calls
 */
public class BukkitCommandDelegate extends BukkitCommand {

    private final Arguments arguments;
    private final Object delegate;

    private final Command command;

    public BukkitCommandDelegate(Object delegate, Command command, Arguments arguments) {
        super(arguments.getBasePath(), command.description(), command.usage(), Arrays.asList(command.aliases()));
        this.setPermission(command.permission());
        this.setPermissionMessage(command.noPermission());
        this.delegate = delegate;
        this.arguments = arguments;
        this.command = command;
    }

    @Override
    public boolean execute(CommandSender sender, String commandLabel, String[] args) {
        Argument[] arguments = this.arguments.lookup(args); // lookup which command we are referring to
        if(arguments.length == 0 || arguments[0] == null) { // invalid command
            return false;
        }

        // build arguments into a path for the command
        String path = ArgumentParser.toPath(arguments);
        Method method = this.arguments.getMethods().get(path);

        if(method == null) { // invalid command
            throw new IllegalStateException("Command path not registered: " + path);
        }

        try {
            Map<String, Object> parsed = ArgumentParser.parseArguments(arguments, args); // parse dynamic arguments
            // method should have signature: public ? method(? extends CommandContext)
            method.invoke(delegate, new CommandContext(parsed, args, commandLabel, sender));
        } catch (IllegalArgumentException illegal) {
            // inform sender that argument is not parsable
            sender.sendMessage(illegal.getMessage());
            return false;
        } catch (InvocationTargetException | IllegalAccessException e) {
            sender.sendMessage("An internal error occurred while executing this command.");
            e.printStackTrace();
        }

        return false;
    }
}
