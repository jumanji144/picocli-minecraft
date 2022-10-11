package me.darknet.cli.util;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class Reflection {

    @SuppressWarnings("unchecked")
    public static <T> T invoke(Object obj, String descriptor, Object... args)
            throws InvocationTargetException, IllegalAccessException, NoSuchMethodException, ClassNotFoundException {
        Method method = lookupMethod(obj.getClass(), descriptor);
        method.setAccessible(true);
        return (T) method.invoke(obj, args);
    }

    public static Method lookupMethod(Class<?> clazz, String descriptor) throws ClassNotFoundException, NoSuchMethodException {
        int descriptorIndex = descriptor.indexOf('(');
        String name = descriptor.substring(0, descriptorIndex);
        descriptor = descriptor.substring(descriptorIndex);
        Type[] types = Type.getArgumentTypes(descriptor);
        Class<?>[] classes = new Class<?>[types.length];
        for (int i = 0; i < types.length; i++) {
            classes[i] = Class.forName(types[i].getClassName());
        }
        return clazz.getDeclaredMethod(name, classes);
    }
    
    public static Field lookupField(Class<?> clazz, String name) throws NoSuchFieldException {
        return clazz.getDeclaredField(name);
    }

    public static Object fieldGet(Object obj, String name) throws NoSuchFieldException, IllegalAccessException {
        Field field = lookupField(obj.getClass(), name);
        field.setAccessible(true);
        return field.get(obj);
    }

    public static Class<?> lookupClass(String name) throws ClassNotFoundException {
        // check if it's a primitive type
        switch (name) {
            case "boolean":
                return boolean.class;
            case "byte":
                return byte.class;
            case "char":
                return char.class;
            case "short":
                return short.class;
            case "int":
                return int.class;
            case "long":
                return long.class;
            case "float":
                return float.class;
            case "double":
                return double.class;
            case "void":
                return void.class;
        }
        String[] arrays = name.split("\\[]");
        if(arrays.length > 0) {
            Class<?> clazz = Class.forName(arrays[0]);
            for (int i = 1; i < arrays.length; i++) {
                clazz = getArrayClass(clazz);
            }
            return clazz;
        }
        return Class.forName(name);
    }

    @SuppressWarnings("unchecked")
    public static <T> Class<? extends T[]> getArrayClass(Class<T> clazz) {
        return (Class<? extends T[]>) Array.newInstance(clazz, 0).getClass();
    }

}
