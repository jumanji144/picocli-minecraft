package me.darknet.cli.impl.bungee;

import net.md_5.bungee.api.ChatColor;
import picocli.CommandLine;

public enum BungeeStyle implements CommandLine.Help.Ansi.IStyle {

    reset(ChatColor.RESET),
    bold(ChatColor.BOLD),
    italic(ChatColor.ITALIC),
    underline(ChatColor.UNDERLINE),
    strikethrough(ChatColor.STRIKETHROUGH),
    reverse(ChatColor.MAGIC),
    black(ChatColor.BLACK),
    red(ChatColor.RED),
    green(ChatColor.GREEN),
    yellow(ChatColor.YELLOW),
    blue(ChatColor.BLUE),
    magenta(ChatColor.LIGHT_PURPLE),
    cyan(ChatColor.AQUA),
    white(ChatColor.WHITE);

    private final ChatColor color;

    BungeeStyle(ChatColor color) {
        this.color = color;
    }

    @Override
    public String on() {
        return color.toString();
    }

    @Override
    public String off() {
        return reset.on();
    }
}
