package org.easysoa.discovery.code;

import java.lang.reflect.AnnotatedElement;

import com.thoughtworks.qdox.model.AbstractBaseJavaEntity;
import com.thoughtworks.qdox.model.JavaClass;

public class ParsingUtils {

    public static boolean hasAnnotation(AbstractBaseJavaEntity entity, String fullyQualifiedAnnotationName) {
        return getAnnotation(entity, fullyQualifiedAnnotationName) != null;
    }
    
    public static com.thoughtworks.qdox.model.Annotation getAnnotation(AbstractBaseJavaEntity entity, String fullyQualifiedAnnotationName) {
        for (com.thoughtworks.qdox.model.Annotation annotation : entity.getAnnotations()) {
            if (fullyQualifiedAnnotationName.equals(annotation.getType().getFullyQualifiedName())) {
                return annotation;
            }
        }
        return null;
    }
    
    public static boolean isTestClass(JavaClass c) {
        return c.getSource().getURL().getPath().contains("src/test/");
    }

    public static boolean hasAnnotation(AnnotatedElement annotable, String fullyQualifiedAnnotationName) {
        for (java.lang.annotation.Annotation annotation : annotable.getAnnotations()) {
            if (fullyQualifiedAnnotationName.equals(annotation.annotationType().getName())) {
                return true;
            }
        }
        return false;
    }
}
