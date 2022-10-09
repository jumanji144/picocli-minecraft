package me.darknet.commandsystem.command;

public class Argument {
    private final int place;
    private final String name;
    private final String type;

    public Argument(int place, String name, String type) {
        this.place = place;
        this.name = name;
        this.type = type;
    }

    public boolean isDynamic() {
        return this.type != null; // if it is a hardcoded path it will be null
    }

    public int getPlace() {
        return place;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }
}