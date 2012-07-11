package org.easysoa.discovery.code.handler;

import java.util.Collection;

import org.apache.maven.plugin.logging.Log;
import org.easysoa.discovery.code.MavenDeliverable;
import org.easysoa.discovery.rest.model.SoaNode;

import com.thoughtworks.qdox.model.JavaSource;

public interface SourcesHandler {

    public Collection<SoaNode> handleSources(JavaSource[] sources,
            MavenDeliverable mavenDeliverable, Log log);
    
}
