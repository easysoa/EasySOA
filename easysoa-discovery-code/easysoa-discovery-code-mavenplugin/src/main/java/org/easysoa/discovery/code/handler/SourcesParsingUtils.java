package org.easysoa.discovery.code.handler;

import com.thoughtworks.qdox.model.JavaClass;

public class SourcesParsingUtils {

    public static boolean isTestClass(JavaClass c) {
        return c.getSource().getURL().getPath().contains("src/test/");
    }
    
}
