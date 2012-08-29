package org.easysoa.discovery.code;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.logging.Log;
import org.easysoa.discovery.code.handler.JaxRSSourcesHandler;
import org.easysoa.discovery.code.handler.JaxWSSourcesHandler;
import org.easysoa.discovery.code.handler.SourcesHandler;
import org.easysoa.registry.rest.RegistryApi;
import org.easysoa.registry.rest.client.ClientBuilder;
import org.easysoa.registry.rest.client.types.java.MavenDeliverableInformation;
import org.easysoa.registry.rest.marshalling.SoaNodeInformation;

import com.thoughtworks.qdox.JavaDocBuilder;
import com.thoughtworks.qdox.model.JavaSource;

/**
 * Says "Hi" to the user.
 * 
 * @goal discover
 */
public class CodeDiscoveryMojo extends AbstractMojo {

    /**
     * @parameter expression="${project.name}"
     * @required
     */
    private String name;
    
    /**
     * @parameter expression="${project.basedir}"
     * @required
     */
    private File projectDirectory;

    /**
     * @parameter expression="${project.groupId}"
     * @required
     */
    private String groupId;
    
    /**
     * @parameter expression="${project.artifactId}"
     * @required
     */
    private String artifactId;
    
    /**
     * @parameter expression="${project.version}"
     * @required
     */
    private String version;

    private Map<String, SourcesHandler> availableHandlers = new HashMap<String, SourcesHandler>();
    
    public void execute() throws MojoExecutionException {
        try {
        // Init registry client
        ClientBuilder clientBuilder = new ClientBuilder();
        clientBuilder.setCredentials("Administrator", "Administrator");
        clientBuilder.setNuxeoSitesUrl("http://localhost:8080/nuxeo/site");
        RegistryApi registryApi = clientBuilder.constructRegistryApi();
        
        // Init handlers
        Log log = getLog();
        this.availableHandlers.put("JAX-WS", new JaxWSSourcesHandler());
        this.availableHandlers.put("JAX-RS", new JaxRSSourcesHandler());
        
        // TODO Maven extensions in -java-api
        MavenDeliverableInformation mavenDeliverable = new MavenDeliverableInformation(groupId + ":" + artifactId);
        mavenDeliverable.setTitle(name);
        mavenDeliverable.setVersion(version);
        try {
            mavenDeliverable.setProperty("dc:description", projectDirectory.toURI().toURL().toString());
        } catch (MalformedURLException e) {
            log.error("Failed to convert project location to URL", e);
        }
        List<SoaNodeInformation> discoveredNodes = new LinkedList<SoaNodeInformation>();
        discoveredNodes.add(mavenDeliverable);

        // Configure parser
        JavaDocBuilder builder = new JavaDocBuilder();
        builder.addSourceTree(this.projectDirectory);
        
        // Iterate through classes to find WSes
        JavaSource[] sources = builder.getSources();
        for (SourcesHandler handler : availableHandlers.values()) {
            discoveredNodes.addAll(handler.handleSources(sources, mavenDeliverable, log));
        }
        
        // Build and send discovery request
        try {
            for (SoaNodeInformation soaNode : discoveredNodes) {
                registryApi.post(soaNode);
            }
        } catch (IOException e) {
            log.error("Failed to send discovery request", e);
        }
        
        }
        catch (Exception e) {
            throw new MojoExecutionException("Failed to discover SOA documents in code", e);
        }
        
    }

}