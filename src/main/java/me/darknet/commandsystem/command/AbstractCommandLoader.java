package me.darknet.commandsystem.command;

import me.darknet.commandsystem.parser.ArgumentParser;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public abstract class AbstractCommandLoader implements CommandLoader {

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
                Arguments arguments = new Arguments();
                arguments.setBaseName(basePath);
                arguments.registerMethod(annotation.value(), method);
                arguments.registerArguments(args);
                if(!this.registerCommand(annotation, arguments, command)) return false;
                registered.add(command);
            }
        }
        return true;
    }

    @Override
    public boolean unregisterCommand(Object command) {
        if(registered.contains(command)) {
            registered.remove(command);
            return true;
        }
        return false;
    }

    public abstract boolean registerCommand(Command annotation, Arguments arguments, Object command);

    public abstract boolean unregisterCommand0(Object command);

    public abstract void registerParsers();
}
