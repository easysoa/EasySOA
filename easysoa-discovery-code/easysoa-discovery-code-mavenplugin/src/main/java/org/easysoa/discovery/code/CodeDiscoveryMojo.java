package org.easysoa.discovery.code;

import java.io.File;
import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.Map;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.logging.Log;
import org.easysoa.discovery.code.handler.ClassHandler;
import org.easysoa.discovery.code.handler.JaxRSClassHandler;
import org.easysoa.discovery.code.handler.JaxWSClassHandler;

import com.thoughtworks.qdox.JavaDocBuilder;
import com.thoughtworks.qdox.model.JavaClass;
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

    private Map<String, ClassHandler> availableHandlers = new HashMap<String, ClassHandler>();
    
    public void execute() throws MojoExecutionException {
        
        // Init
        Log log = getLog();
        this.availableHandlers.put("JAX-WS", new JaxWSClassHandler());
        this.availableHandlers.put("JAX-RS", new JaxRSClassHandler());
        MavenDeliverable mavenDeliverable;
        try {
            mavenDeliverable = new MavenDeliverable(name,
                    projectDirectory.toURI().toURL(), groupId, artifactId, version);
        } catch (MalformedURLException e) {
            log.error("Failed to convert project location to URL", e);
            mavenDeliverable = new MavenDeliverable(name, null, groupId, artifactId, version);
        }
        
        // Configure parser
        JavaDocBuilder builder = new JavaDocBuilder();
        builder.addSourceTree(this.projectDirectory);
        
        // Iterate through classes
        JavaSource[] sources = builder.getSources();
        for (JavaSource source : sources) {
            JavaClass[] classes = source.getClasses();
            for (JavaClass c : classes) {
                for (ClassHandler handler : availableHandlers.values()) {
                    handler.handleClass(c, mavenDeliverable, log);
                }
            }
        }
        
    }

}