package me.darknet.commandsystem.impl.bukkit;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Map;

public class CommandContext extends me.darknet.commandsystem.command.CommandContext {

    private final CommandSender sender;

    public CommandContext(Map<String, Object> parsedArguments, String[] pureArguments, String label, CommandSender sender) {
        super(parsedArguments, pureArguments, label);
        this.sender = sender;
    }

    // Helper methods
    public boolean isPlayer() {
        return this.sender instanceof Player;
    }

    public Player getPlayer() {
        if(isPlayer()) return (Player) this.sender;
        return null;
    }

    public void sendMessage(String message) {
        this.sender.sendMessage(message);
    }

    public CommandSender getSender() {
        return this.sender;
    }

}
