package me.darknet.commandsystem.parser;

import java.util.ArrayList;

public class ArgumentTree {

    private final ArgumentNode root;

    public ArgumentTree() {
        this.root = new ArgumentNode(null, null, new ArrayList<>());
    }

    public ArgumentNode getRoot() {
        return root;
    }

}
