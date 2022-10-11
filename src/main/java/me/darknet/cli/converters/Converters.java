package me.darknet.cli.converters;

import picocli.CommandLine;

import java.util.HashMap;
import java.util.Map;

public class Converters {

    private static final Map<Class, CommandLine.ITypeConverter> registeredConverterInstances = new HashMap<>();

    public static <T> void register(Class<T> converterTargetClass, CommandLine.ITypeConverter<T> converter) {
        registeredConverterInstances.put(converterTargetClass, converter);
    }

    public static void registerInto(CommandLine line) {
        registeredConverterInstances.forEach(line::registerConverter);
    }

}
