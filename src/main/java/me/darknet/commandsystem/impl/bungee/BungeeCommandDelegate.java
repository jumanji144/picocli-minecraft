package me.darknet.commandsystem.impl.bungee;

import me.darknet.commandsystem.command.Argument;
import me.darknet.commandsystem.command.Arguments;
import me.darknet.commandsystem.parser.ArgumentParser;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.plugin.Command;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;

public class BungeeCommandDelegate extends Command {

    private final Object delegate;
    private final Arguments arguments;

    private final Map<String, me.darknet.commandsystem.command.Command> commandMap;

    public BungeeCommandDelegate(Object delegate, Map<String, me.darknet.commandsystem.command.Command> commandMap, Arguments arguments) {
        super(arguments.getBasePath());
        this.delegate = delegate;
        this.commandMap = commandMap;
        this.arguments = arguments;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        Argument[] arguments = this.arguments.lookup(args); // lookup which command we are referring to
        if(arguments.length == 0 || arguments[0] == null) { // invalid command
            return;
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
            method.invoke(delegate, new CommandContext(parsed, args, "", sender));
        } catch (IllegalArgumentException illegal) {
            // inform sender that argument is not parsable
            sender.sendMessage(illegal.getMessage());
            return;
        } catch (InvocationTargetException | IllegalAccessException e) {
            sender.sendMessage("An internal error occurred while executing this command.");
            e.printStackTrace();
        }
    }
}
