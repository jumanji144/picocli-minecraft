package me.darknet.cli.impl.bungee;

import me.darknet.cli.command.Base;
import net.md_5.bungee.api.CommandSender;

public abstract class BungeeBase extends Base {

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
