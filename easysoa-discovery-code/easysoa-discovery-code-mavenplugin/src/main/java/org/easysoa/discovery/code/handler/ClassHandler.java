package org.easysoa.discovery.code.handler;

import java.util.Collection;

import org.apache.maven.plugin.logging.Log;
import org.easysoa.discovery.code.MavenDeliverable;
import org.easysoa.discovery.rest.model.SOANode;

import com.thoughtworks.qdox.model.JavaClass;

public interface ClassHandler {

    public Collection<SOANode> handleClass(JavaClass c, MavenDeliverable mavenDeliverable, Log log);
    
}
