package me.darknet.commandsystem.parser;

import me.darknet.commandsystem.command.Argument;

import java.util.ArrayList;
import java.util.List;

public class ArgumentNode {

    private final String name; // easier identifier
    private final Argument entry;
    private final ArgumentNode parent;
    private final List<ArgumentNode> children;

    public ArgumentNode(Argument entry, ArgumentNode parent, List<ArgumentNode> children) {
        this.name = entry == null ? "root" : entry.getName();
        this.entry = entry;
        this.parent = parent;
        this.children = children;
    }

    public Argument getEntry() {
        return entry;
    }

    public ArgumentNode getParent() {
        return parent;
    }

    public List<ArgumentNode> getChildren() {
        return children;
    }

    public String getName() {
        return name;
    }

    public ArgumentNode getNode(String name) {
        for (ArgumentNode node : children) {
            if (node.getName().equals(name)) {
                return node;
            }
        }
        return null;
    }

    public void addChild(ArgumentNode node) {
        // do deep tree merge
        ArgumentNode existing = getNode(node.getName());
        if (existing != null) {
            for (ArgumentNode child : node.getChildren()) {
                existing.addChild(child);
            }
        } else {
            children.add(node);
        }
    }

    public static ArgumentNode toNode(Argument[] arguments) {
        ArgumentNode root = new ArgumentNode(arguments[0], null, new ArrayList<>());
        ArgumentNode current = root;
        for (int i = 1; i < arguments.length; i++) {
            ArgumentNode node = new ArgumentNode(arguments[i], current, new ArrayList<>());
            current.addChild(node);
            current = node;
        }
        return root;
    }
}
