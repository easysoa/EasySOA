package org.easysoa.discovery.code.handler;

import org.apache.maven.plugin.logging.Log;
import org.easysoa.discovery.code.DeliverableInfo;

import com.thoughtworks.qdox.model.JavaClass;

public interface ClassHandler {

    public void handleClass(JavaClass c, DeliverableInfo deliverableInfo, Log log);
    
}
