package me.darknet.cli.impl.bukkit;

import me.darknet.cli.command.Base;
import org.bukkit.command.CommandSender;

public abstract class BukkitBase extends Base {

    private CommandSender sender;
    private String[] args;

    public CommandSender getSender() {
        return sender;
    }

    public String[] getArgs() {
        return args;
    }

    public void setSender(CommandSender sender) {
        this.sender = sender;
    }

    public void setArgs(String[] args) {
        this.args = args;
    }
}
