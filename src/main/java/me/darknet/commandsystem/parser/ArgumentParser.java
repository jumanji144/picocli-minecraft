package me.darknet.commandsystem.parser;

import me.darknet.commandsystem.command.Argument;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class ArgumentParser {

    private static final Map<String, Function<String, ?>> parsers = new HashMap<>();

    static {
        // register primitive parsers
        parsers.put("int", Integer::parseInt);
        parsers.put("long", Long::parseLong);
        parsers.put("float", Float::parseFloat);
        parsers.put("double", Double::parseDouble);
        parsers.put("boolean", Boolean::parseBoolean);
        parsers.put("byte", Byte::parseByte);
        parsers.put("short", Short::parseShort);
        parsers.put("char", s -> s.charAt(0));
    }

    public static Argument[] parseArguments(String path) {
        String[] args = path.split("\\.");
        Argument[] arguments = new Argument[args.length];
        for (int i = 0; i < args.length; i++) {
            String arg = args[i];
            if (arg.startsWith("<") && arg.endsWith(">")) {
                String[] argSplit = arg.substring(1, arg.length() - 1).split(":");
                if (argSplit.length == 2) {
                    arguments[i] = new Argument(i, argSplit[0], argSplit[1]);
                } else {
                    arguments[i] = new Argument(i, argSplit[0], null);
                }
            } else {
                arguments[i] = new Argument(i, arg, null);
            }
        }
        return arguments;
    }

    public static Map<String, Object> parseArguments(Argument[] arguments, String[] input) {
        // clamp the max size of the input
        int size = Math.min(arguments.length, input.length);
        Map<String, Object> results = new HashMap<>();
        for (int i = 0; i < size; i++) {
            Argument argument = arguments[i];
            if (argument.isDynamic()) {
                // first try to look up a parser
                Function<String, ?> parser = parsers.get(argument.getType().toLowerCase());
                if (parser != null) {
                    // parse the input
                    Object parsed = parser.apply(input[i]);
                    results.put(argument.getName(), parsed);
                } else {
                    results.put(argument.getName(), input[i]);
                }
            }
        }
        return results;
    }

    public static String toPath(Argument[] arguments) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < arguments.length; i++) {
            Argument argument = arguments[i];
            if (argument.isDynamic()) {
                builder.append("<").append(argument.getName()).append(":").append(argument.getType()).append(">");
            } else {
                builder.append(argument.getName());
            }
            if (i != arguments.length - 1) {
                builder.append(".");
            }
        }
        return builder.toString();
    }

    public static void registerParser(String type, Function<String, ?> parser) {
        parsers.put(type, parser);
    }
}
