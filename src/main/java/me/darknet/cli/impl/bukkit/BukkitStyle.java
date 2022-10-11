package me.darknet.cli.impl.bukkit;

import org.bukkit.ChatColor;
import picocli.CommandLine;

public enum BukkitStyle implements CommandLine.Help.Ansi.IStyle {

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
    white(ChatColor.WHITE),
    reset(ChatColor.RESET);


    private final ChatColor color;

    BukkitStyle(ChatColor color) {
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
