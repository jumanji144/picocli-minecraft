import me.darknet.cli.util.Reflection;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class ReflectionTest {

    @Test
    public void testObjectLookup() {
        try {
            Class<?> clazz = Reflection.lookupClass("java.lang.Object");
            Assertions.assertEquals(Object.class, clazz);
        } catch (ClassNotFoundException e) {
            Assertions.fail("Object class not found");
        }
    }

    @Test
    public void testMethodLookup() {
        try {
            Class<?> clazz = Reflection.lookupClass("java.lang.Object");
            Method method = Reflection.lookupMethod(clazz, "toString()Ljava/lang/String;");
            Assertions.assertEquals(Object.class, method.getDeclaringClass());
            Assertions.assertEquals("toString", method.getName());
            Assertions.assertEquals(String.class, method.getReturnType());
            // array return type
            method = Reflection.lookupMethod(Reflection.lookupClass("java.lang.reflect.Method"), "getExceptionTypes()[Ljava/lang/Class;");
            Assertions.assertEquals(Class[].class, method.getReturnType());
        } catch (ClassNotFoundException | NoSuchMethodException e) {
            Assertions.fail("Object class not found", e);
        }
    }

    @Test
    public void testMethodInvoke() {
        try {
            Class<?> clazz = Reflection.lookupClass("java.lang.Object");
            Method method = Reflection.lookupMethod(clazz, "toString()Ljava/lang/String;");
            Assertions.assertEquals(Object.class, method.getDeclaringClass());
            Assertions.assertEquals("toString", method.getName());
            Assertions.assertEquals(String.class, method.getReturnType());
            Object obj = new Object();
            String result = Reflection.invoke(obj, "toString()Ljava/lang/String;");
            Assertions.assertEquals(obj.toString(), result);
        } catch (ClassNotFoundException | NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
            Assertions.fail("Object class not found", e);
        }
    }

}
