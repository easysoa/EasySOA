package org.easysoa.discovery.code;

import java.io.File;
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
        DeliverableInfo projectInfo = new DeliverableInfo(name, projectDirectory, groupId, artifactId, version);
        this.availableHandlers.put("JAX-WS", new JaxWSClassHandler());
        this.availableHandlers.put("JAX-RS", new JaxRSClassHandler());
        Log log = getLog();
        
        // Configure parser
        JavaDocBuilder builder = new JavaDocBuilder();
        builder.addSourceTree(this.projectDirectory);
        
        // Iterate through classes
        JavaSource[] sources = builder.getSources();
        for (JavaSource source : sources) {
            JavaClass[] classes = source.getClasses();
            for (JavaClass c : classes) {
                for (ClassHandler handler : availableHandlers.values()) {
                    handler.handleClass(c, projectInfo, log);
                }
            }
        }
        
    }

}