package me.darknet.commandsystem.command;

import me.darknet.commandsystem.parser.ArgumentNode;
import me.darknet.commandsystem.parser.ArgumentTree;

import java.lang.reflect.Method;
import java.util.*;

public class Arguments {

    private final ArgumentTree builtTree = new ArgumentTree();
    private final Map<String, Method> methods = new HashMap<>(); // stores registered paths
    private String baseName;

    public boolean lookup(String[] paths, int index, List<ArgumentNode> nodes, List<Argument> track) {
        if(index == paths.length) {
            return true;
        }
        String path = paths[index];
        for(ArgumentNode node : nodes) {
            if (node.getEntry().isDynamic()) {
                List<Argument> pathTrack = new ArrayList<>();
                if(lookup(paths, index + 1, node.getChildren(), pathTrack)) {
                    track.add(node.getEntry());
                    track.addAll(pathTrack);
                    return true;
                }
            } else if (node.getName().equalsIgnoreCase(path)) {
                track.add(node.getEntry());
                if(lookup(paths, index + 1, node.getChildren(), track)) {
                    return true;
                }
            }
        }
        return false;
    }

    public Argument[] lookup(String[] paths) {
        List<Argument> track = new ArrayList<>();
        lookup(paths, 0, builtTree.getRoot().getChildren(), track);
        return track.toArray(new Argument[0]);
    }

    public String getBasePath() {
        return baseName;
    }

    public ArgumentTree getBuiltTree() {
        return builtTree;
    }

    public Map<String, Method> getMethods() {
        return methods;
    }

    public void setBaseName(String baseName) {
        this.baseName = baseName;
    }

    public void registerMethod(String path, Method method) {
        this.methods.put(path, method);
    }

    public void registerArguments(Argument... arguments) {
        ArgumentNode node = ArgumentNode.toNode(arguments);
        builtTree.getRoot().addChild(node);
    }
}
