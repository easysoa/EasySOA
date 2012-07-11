package org.easysoa.discovery.code.handler;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import org.apache.maven.plugin.logging.Log;
import org.easysoa.discovery.code.MavenDeliverable;
import org.easysoa.discovery.code.ParsingUtils;
import org.easysoa.discovery.rest.model.ServiceImpl;
import org.easysoa.discovery.rest.model.SoaNode;
import org.easysoa.discovery.rest.model.SoaNodeType;

import com.thoughtworks.qdox.model.JavaClass;
import com.thoughtworks.qdox.model.JavaMethod;
import com.thoughtworks.qdox.model.JavaSource;

public class JaxRSSourcesHandler implements SourcesHandler {

    private static final String ANN_PATH = "javax.ws.rs.Path";
    private static final String[] ANN_METHODS = new String[] {
        "javax.ws.rs.GET", "javax.ws.rs.POST", "javax.ws.rs.PUT",
        "javax.ws.rs.HEAD", "javax.ws.rs.OPTIONS"
      };

    public Collection<SoaNode> handleSources(JavaSource[] sources, 
            MavenDeliverable deliverable, Log log) {
        List<SoaNode> discoveredNodes = new LinkedList<SoaNode>();

        // Explore each class
        for (JavaSource source : sources) {
            JavaClass[] classes = source.getClasses();
            for (JavaClass c : classes) {
                discoveredNodes.addAll(this.handleClass(c, sources, deliverable, log));
            }
        }
        return discoveredNodes;
    }
        
    public Collection<SoaNode> handleClass(JavaClass c, JavaSource[] sources, 
            MavenDeliverable deliverable, Log log) {
        List<SoaNode> discoveredNodes = new LinkedList<SoaNode>();
        
        // Check JAX-RS annotation
        if (ParsingUtils.hasAnnotation(c, ANN_PATH)) {
            // Extract WS info
            ServiceImpl serviceImpl = new ServiceImpl(deliverable,
                    "JAX-RS", c.getFullyQualifiedName(), c.getName());
            deliverable.addRelation(SoaNodeType.ServiceImpl, serviceImpl.getId());
            discoveredNodes.add(serviceImpl);
            
            // Extract operations info
            StringBuilder operationsInfo = new StringBuilder();
            for (JavaMethod method : c.getMethods()) {
                if (ParsingUtils.hasAnnotation(method, ANN_PATH)) {
                    // Extract service path
                    Object path = ParsingUtils.getAnnotation(method, ANN_PATH).getProperty("value");
                    
                    // Extract HTTP method
                    String httpMethod = "???";
                    for (String annHttpMethod : ANN_METHODS) {
                        if (ParsingUtils.hasAnnotation(method, annHttpMethod)) {
                            httpMethod = annHttpMethod.replace("javax.ws.rs.", "");
                            break;
                        }
                    }
                    
                    operationsInfo.append(httpMethod + " Operation " + method.getName() + " (" + path + "), ");
                }
            }
            if (operationsInfo.length() > 0) {
                // Cosmetic changes before storage
                String operationInfoString = operationsInfo.toString().substring(0, operationsInfo.length() - 2);
                operationInfoString = operationInfoString.replace("\"", "'");
                serviceImpl.setOperationsInfo(operationInfoString);
            }
        }
        
        return discoveredNodes;
        
    }

}
