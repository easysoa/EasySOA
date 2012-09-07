package org.easysoa.discovery.code.handler;

import java.util.Collection;

import org.apache.maven.plugin.logging.Log;
import org.easysoa.discovery.code.CodeDiscoveryRegistryClient;
import org.easysoa.registry.rest.client.types.java.MavenDeliverableInformation;
import org.easysoa.registry.rest.marshalling.SoaNodeInformation;

import com.thoughtworks.qdox.model.JavaSource;

public interface SourcesHandler {

    public Collection<SoaNodeInformation> handleSources(JavaSource[] sources,
            MavenDeliverableInformation mavenDeliverable,
            CodeDiscoveryRegistryClient registryClient, Log log) throws Exception ;
    
}
