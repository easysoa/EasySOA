package org.easysoa.discovery.code;

import com.thoughtworks.qdox.model.AbstractBaseJavaEntity;
import com.thoughtworks.qdox.model.Annotation;

public class ParsingUtils {

    public static boolean hasAnnotation(AbstractBaseJavaEntity entity, String fullyQualifiedAnnotationName) {
        return getAnnotation(entity, fullyQualifiedAnnotationName) != null;
    }
    
    public static Annotation getAnnotation(AbstractBaseJavaEntity entity, String fullyQualifiedAnnotationName) {
        for (Annotation annotation : entity.getAnnotations()) {
            if (fullyQualifiedAnnotationName.equals(annotation.getType().getFullyQualifiedName())) {
                return annotation;
            }
        }
        return null;
    }
}
