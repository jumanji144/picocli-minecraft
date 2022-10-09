package me.darknet.commandsystem.command;

import me.darknet.commandsystem.parser.ArgumentParser;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class AbstractCommandLoader implements CommandLoader {

    public AbstractCommandLoader() {
        this.registerParsers();
    }

    List<Object> registered = new ArrayList<>();

    public String getBase(String path) {
        if(path.indexOf('.') == -1) {
            return path;
        }
        return path.substring(0, path.indexOf('.'));
    }

    @Override
    public boolean registerCommand(Object command) {
        Method[] methods = command.getClass().getMethods();
        Map<String, Arguments> argumentMap = new HashMap<>();
        Map<String, Map<String, Command>> commandMap = new HashMap<>();
        for(Method method : methods) {
            if(method.isAnnotationPresent(Command.class)) {
                Command annotation = method.getAnnotation(Command.class);
                String path = annotation.value(); // first path entry is the base path
                String basePath = path;
                if(path.indexOf('.') != -1) {
                    basePath = path.substring(0, path.indexOf('.'));
                    path = path.substring(path.indexOf('.') + 1);
                }
                path = path.substring(path.indexOf(".") + 1);
                Argument[] args = ArgumentParser.parseArguments(path);
                String finalBasePath = basePath;
                Arguments arguments = argumentMap.computeIfAbsent(basePath, k -> {
                    Arguments a = new Arguments();
                    a.setBaseName(finalBasePath);
                    return a;
                });
                arguments.getMethods().put(path, method);
                arguments.registerArguments(args);
                Map<String, Command> commandList = commandMap.computeIfAbsent(basePath, k -> new HashMap<>());
                commandList.put(path, annotation);
                registered.add(command);
            }
        }
        argumentMap.forEach((name, arguments) -> this.registerCommand(commandMap.get(name), arguments, command));
        return true;
    }

    @Override
    public boolean unregisterCommand(Object command) {
        if(registered.contains(command)) {
            registered.remove(command);
            return unregisterCommand0(command);
        }
        return false;
    }

    @Override
    public void unregisterAll() {
        registered.forEach(this::unregisterCommand);
    }

    public abstract boolean registerCommand(Map<String, Command> commandMap, Arguments arguments, Object command);

    public abstract boolean unregisterCommand0(Object command);

    public abstract void registerParsers();
}
