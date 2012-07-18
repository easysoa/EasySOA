package org.easysoa.discovery.code.handler;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import org.apache.maven.plugin.logging.Log;
import org.easysoa.discovery.code.MavenDeliverable;
import org.easysoa.discovery.code.ParsingUtils;
import org.easysoa.discovery.rest.model.Service;
import org.easysoa.discovery.rest.model.ServiceImpl;
import org.easysoa.discovery.rest.model.SoaNode;
import org.easysoa.discovery.rest.model.SoaNodeType;

import com.thoughtworks.qdox.model.JavaClass;
import com.thoughtworks.qdox.model.JavaMethod;
import com.thoughtworks.qdox.model.JavaSource;

/**
 * In project & current deliverable, reports :
 * * JAXRS service impl and their impl'd service interface if any (from impl and also by themselves),
 * both by having class or method(s) annotated by @Path
 * * thanks to InterfaceHandlerBase, member (field or bean setter)-injected service :
 * typed by interfaces having class or method(s) annotated by @Path
 */
public class JaxRSSourcesHandler extends InterfaceHandlerBase implements SourcesHandler {

    private static final String ANN_PATH = "javax.ws.rs.Path";
    private static final String[] ANN_METHODS = new String[] {
        "javax.ws.rs.GET", "javax.ws.rs.POST", "javax.ws.rs.PUT",
        "javax.ws.rs.HEAD", "javax.ws.rs.OPTIONS"
      };

    public Collection<SoaNode> handleSources(JavaSource[] sources, 
            MavenDeliverable mavenDeliverable, Log log) {
        List<SoaNode> discoveredNodes = new LinkedList<SoaNode>();

        // Pass 1 : Find all WS interfaces if any
        for (JavaSource source : sources) {
            // TODO diff between main & tests
            JavaClass[] classes = source.getClasses();
            for (JavaClass c : classes) {
                if (c.isInterface()) {
                    // Check JAX-RS annotation
                    ArrayList<JavaMethod> pathMethods = null;
                    for (JavaMethod method : c.getMethods()) {
                        if (ParsingUtils.hasAnnotation(method, ANN_PATH)) {
                            if (pathMethods == null) {
                                pathMethods = new ArrayList<JavaMethod>(c.getMethods().length);
                            }
                            pathMethods.add(method);
                        }
                    }
                    if (pathMethods != null || ParsingUtils.hasAnnotation(c, ANN_PATH)) {
                        wsClientsAndItfs.add(c);
                        
                        // also in first pass for itf, Extract WS info
                        Service serviceDef = new Service(c.getName(), mavenDeliverable.getVersion());
                        discoveredNodes.add(serviceDef);
                    }
                }
            }
        }

        // Pass 2 : Find all WS impl, including those implementing known interfaces (though its not "classical" JAXRS)
        for (JavaSource source : sources) {
            // TODO diff between main & tests
            JavaClass[] classes = source.getClasses();
            for (JavaClass c : classes) {
                if (!c.isInterface()) {
                    discoveredNodes.addAll(this.handleClass(c, sources, mavenDeliverable, log));
                }
            }
        }
        
        return discoveredNodes;
    }
        
    public Collection<SoaNode> handleClass(JavaClass c, JavaSource[] sources, 
            MavenDeliverable deliverable, Log log) {
        List<SoaNode> discoveredNodes = new LinkedList<SoaNode>();
        
        JavaClass itf = getWsItf(c);

        ArrayList<JavaMethod> pathMethods = null;
        if (itf == null) {
            // Check JAX-RS annotation
            for (JavaMethod method : c.getMethods()) {
                if (ParsingUtils.hasAnnotation(method, ANN_PATH)) {
                    if (pathMethods == null) {
                        pathMethods = new ArrayList<JavaMethod>(c.getMethods().length);
                    }
                    pathMethods.add(method);
                }
            }
        }

        if (itf != null || pathMethods != null || ParsingUtils.hasAnnotation(c, ANN_PATH)) {
            
            // Extract WS info
            ServiceImpl serviceImpl = new ServiceImpl(deliverable,
                    "JAX-RS", c.getFullyQualifiedName(), c.getName());
            deliverable.addRelation(SoaNodeType.ServiceImpl, serviceImpl.getId());
            discoveredNodes.add(serviceImpl);
            
            if (itf != null) {
                // Extract WS info
                Service serviceDef = new Service(itf.getName(), deliverable.getVersion()); // TODO + Path
                serviceDef.addRelation(serviceImpl);
                discoveredNodes.add(serviceDef);
            }
            
            // Extract operations info
            StringBuilder operationsInfo = new StringBuilder();
            if (pathMethods != null) {
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
        }

        // member injected by service (Path) annotated interfaces :
        handleInjectedMembers(c, deliverable, log);
        
        return discoveredNodes;
        
    }

    private JavaClass getWsItf(JavaClass c) {
        for (JavaClass itf : c.getImplementedInterfaces()) {
            for (JavaClass wsItf : wsClientsAndItfs) {
                if (itf.getFullyQualifiedName().equals(wsItf)) {
                    return itf;
                }
            }
        }
        return null;
    }

}
