package org.easysoa.discovery.code;

import java.io.IOException;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.maven.model.Dependency;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.project.MavenProject;
import org.easysoa.discovery.code.handler.JaxRSSourcesHandler;
import org.easysoa.discovery.code.handler.JaxWSSourcesHandler;
import org.easysoa.discovery.code.handler.SourcesHandler;
import org.easysoa.registry.rest.RegistryApi;
import org.easysoa.registry.rest.client.ClientBuilder;
import org.easysoa.registry.rest.client.types.java.MavenDeliverableInformation;
import org.easysoa.registry.rest.marshalling.OperationResult;
import org.easysoa.registry.rest.marshalling.SoaNodeInformation;
import org.easysoa.registry.types.Deliverable;

import com.thoughtworks.qdox.JavaDocBuilder;
import com.thoughtworks.qdox.model.JavaSource;

/**
 * Allows to discover services information by parsing the sources of a project.
 * All discoveries are then sent to Nuxeo through its REST API. 
 * 
 * @goal discover
 */
public class CodeDiscoveryMojo extends AbstractMojo {

    /**
     * @parameter expression="${project}"
     * @required
     * @readonly
     */
    private MavenProject project;

    /**
     * @parameter default-value="http://localhost:8080/nuxeo/site"
     */
    private String nuxeoSitesUrl;
    
    /**
     * @parameter default-value="Administrator"
     */
    private String username;
    
    /**
     * @parameter default-value="Administrator"
     */
    private String password;
    
    /**
     * @parameter
     */
    private String application;
    
    private Map<String, SourcesHandler> availableHandlers = new HashMap<String, SourcesHandler>();
    
    public void execute() throws MojoExecutionException {
        try {
        // Init registry client
        ClientBuilder clientBuilder = new ClientBuilder();
        clientBuilder.setCredentials(username, password);
        clientBuilder.setNuxeoSitesUrl(nuxeoSitesUrl);
        RegistryApi registryApi = clientBuilder.constructRegistryApi();
        
        // Init handlers
        Log log = getLog();
        this.availableHandlers.put("JAX-WS", new JaxWSSourcesHandler());
        this.availableHandlers.put("JAX-RS", new JaxRSSourcesHandler());
        
        MavenDeliverableInformation mavenDeliverable = new MavenDeliverableInformation(
                project.getGroupId() + ":" + project.getArtifactId());
        mavenDeliverable.setTitle(project.getName());
        mavenDeliverable.setVersion(project.getVersion());
        if (application != null && !application.trim().isEmpty()) {
            mavenDeliverable.setApplication(application);
        }
        try {
            mavenDeliverable.setProperty(Deliverable.XPATH_LOCATION, project.getBasedir().toURI().toURL().toString());
        } catch (MalformedURLException e) {
            log.error("Failed to convert project location to URL", e);
        }
        try {
            // List dependencies
            List<?> dependencies = project.getDependencies();
            List<String> formattedDependencies = new ArrayList<String>();
            for (Object objectDependency : dependencies) {
                Dependency d = (Dependency) objectDependency;
                formattedDependencies.add(d.getGroupId() + ":" + d.getArtifactId() + ":" + d.getVersion());
            }
            mavenDeliverable.setProperty(Deliverable.XPATH_DEPENDENCIES, (Serializable) formattedDependencies);
        } catch (MalformedURLException e) {
            log.error("Failed to convert project location to URL", e);
        }
        List<SoaNodeInformation> discoveredNodes = new ArrayList<SoaNodeInformation>();
        discoveredNodes.add(mavenDeliverable);

        // Configure parser
        JavaDocBuilder builder = new JavaDocBuilder();
        builder.addSourceTree(project.getBasedir());
        
        // Iterate through classes to find WSes
        JavaSource[] sources = builder.getSources();
        CodeDiscoveryRegistryClient registryClient = new CodeDiscoveryRegistryClient(registryApi);
        for (SourcesHandler handler : availableHandlers.values()) {
            discoveredNodes.addAll(handler.handleSources(sources, mavenDeliverable, registryClient, log));
        }
        
        // Build and send discovery request
        try {
            log.info("Sending found SoaNodes to " + nuxeoSitesUrl);
            if (discoveredNodes.size() > 0) {
                for (SoaNodeInformation soaNode : discoveredNodes) {
                    log.info("> " + soaNode.toString());
                    OperationResult result = registryApi.post(soaNode);
                    if (!result.isSuccessful()) {
                        IOException ioException = new IOException(result.getMessage());
                        ioException.setStackTrace(result.getStacktrace());
                    	throw ioException;
                    }
                }
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