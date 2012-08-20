package org.easysoa.registry.test;

import java.lang.reflect.Method;

import javax.ws.rs.Path;

public class PathExtractor {

    public static String getPath(Class<?> c) {
        Path annotation = c.getAnnotation(Path.class);
        return annotation.value();
    }

    public static String getPath(Class<?> c, String methodName, Class<?>... parameterTypes)
            throws SecurityException, NoSuchMethodException {
        Method method = c.getDeclaredMethod(methodName, parameterTypes);
        return method.getAnnotation(Path.class).value();
    }
}
