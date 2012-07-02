package org.easysoa.discovery.code;

import java.io.File;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;

import com.thoughtworks.qdox.JavaDocBuilder;
import com.thoughtworks.qdox.model.AbstractBaseJavaEntity;
import com.thoughtworks.qdox.model.Annotation;
import com.thoughtworks.qdox.model.JavaClass;
import com.thoughtworks.qdox.model.JavaMethod;
import com.thoughtworks.qdox.model.JavaParameter;
import com.thoughtworks.qdox.model.JavaSource;

/**
 * Says "Hi" to the user.
 * 
 * @goal discover
 */
public class CodeDiscoveryMojo extends AbstractMojo {

    /**
     * Location of the sources directory.
     * @parameter expression="${project.basedir}/src"
     * @required
     */
    private File sourcesDirectory;
    
    public void execute() throws MojoExecutionException {
        
        // Configure parser
        JavaDocBuilder builder = new JavaDocBuilder();
        builder.addSourceTree(sourcesDirectory);
        
        // Iterate through classes
        JavaSource[] sources = builder.getSources();
        for (JavaSource source : sources) {
            JavaClass[] classes = source.getClasses();
            for (JavaClass c : classes) {
                analyzeClass(c);
            }
        }
        
    }

    private void analyzeClass(JavaClass c) {
        StringBuilder classInfo = new StringBuilder();
        
        // Check JAX-WS annotation
        if (c.isInterface() && hasAnnotation(c, "javax.jws.WebService")) {
            // Extract WS info
            classInfo.append("WebService " + c.getName() + "\n");
            classInfo.append("----------------------------\n");
            
            // Exctract operations info
            for (JavaMethod method : c.getMethods()) {
                if (hasAnnotation(method, "javax.jws.WebResult")) {
                    Annotation webResultAnn = getAnnotation(method, "javax.jws.WebResult");
                    classInfo.append("* Operation " + webResultAnn.getProperty("name") + "\n");
                    
                    // Extract parameters info
                    for (JavaParameter parameter : method.getParameters()) {
                        Annotation webParamAnn = getAnnotation(parameter, "javax.jws.WebParam");
                        classInfo.append("  " + webParamAnn.getProperty("name").getParameterValue()
                                + " : " + parameter.getType().toString() + "\n");
                    }
                }
            }
            classInfo.append("----------------------------\n\n");
        }
        
        getLog().info(classInfo.toString());
    }
    
    private boolean hasAnnotation(AbstractBaseJavaEntity entity, String fullyQualifiedAnnotationName) {
        return getAnnotation(entity, fullyQualifiedAnnotationName) != null;
    }
    
    private Annotation getAnnotation(AbstractBaseJavaEntity entity, String fullyQualifiedAnnotationName) {
        for (Annotation annotation : entity.getAnnotations()) {
            if (fullyQualifiedAnnotationName.equals(annotation.getType().getFullyQualifiedName())) {
                return annotation;
            }
        }
        return null;
    }
}