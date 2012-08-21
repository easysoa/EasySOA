package org.easysoa.discovery.code;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.auth.AuthScope;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.logging.Log;
import org.easysoa.discovery.code.handler.JaxRSSourcesHandler;
import org.easysoa.discovery.code.handler.JaxWSSourcesHandler;
import org.easysoa.discovery.code.handler.SourcesHandler;
import org.easysoa.discovery.code.model.MavenDeliverable;
import org.easysoa.discovery.code.model.SoaNode;

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

        // Init handlers
        Log log = getLog();
        this.availableHandlers.put("JAX-WS", new JaxWSSourcesHandler());
        this.availableHandlers.put("JAX-RS", new JaxRSSourcesHandler());
        
        // Deliverable discovery
        MavenDeliverable mavenDeliverable = new MavenDeliverable(groupId, artifactId);
        mavenDeliverable.setTitle(name);
        mavenDeliverable.setProperty("del:nature", "maven");
        try {
            // TODO Set version, create more suitable property to set URL
            mavenDeliverable.setProperty("dc:description", projectDirectory.toURI().toURL().toString());
        } catch (MalformedURLException e) {
            log.error("Failed to convert project location to URL", e);
        }
        List<SoaNode> discoveredNodes = new LinkedList<SoaNode>();
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
            HttpClient client = new HttpClient();
            client.getParams().setAuthenticationPreemptive(true);
            client.getState().setCredentials(AuthScope.ANY,
                    new UsernamePasswordCredentials("Administrator", "Administrator")); // TODO
            for (SoaNode soaNode : discoveredNodes) {
                PostMethod request = new PostMethod("http://localhost:8080/nuxeo/site/easysoa/registry/" + soaNode.getType());
                request.setRequestEntity(new StringRequestEntity(soaNode.toJSONString(), "application/json", "UTF-8"));
                client.executeMethod(request);
            }
        } catch (IOException e) {
            log.error("Failed to send discovery request", e);
        }
        
    }

}