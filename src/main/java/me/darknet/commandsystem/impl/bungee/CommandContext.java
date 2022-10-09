package me.darknet.commandsystem.impl.bungee;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.Map;

public class CommandContext extends me.darknet.commandsystem.command.CommandContext {

    private final CommandSender sender;

    public CommandContext(Map<String, Object> parsedArguments, String[] pureArguments, String label, CommandSender sender) {
        super(parsedArguments, pureArguments, label);
        this.sender = sender;
    }

    // Helper methods
    public boolean isPlayer() {
        return this.sender instanceof ProxiedPlayer;
    }

    public ProxiedPlayer getPlayer() {
        if(isPlayer()) return (ProxiedPlayer) this.sender;
        return null;
    }

    public void sendMessage(String message) {
        this.sender.sendMessage(message);
    }

    public void sendMessage(BaseComponent... message) {
        this.sender.sendMessage(message);
    }

    public CommandSender getSender() {
        return this.sender;
    }


}
