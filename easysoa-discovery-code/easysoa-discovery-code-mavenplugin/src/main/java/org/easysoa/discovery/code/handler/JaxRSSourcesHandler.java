package org.easysoa.discovery.code.handler;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import org.apache.maven.plugin.logging.Log;
import org.easysoa.discovery.code.ParsingUtils;
import org.easysoa.registry.rest.client.types.ServiceImplInformation;
import org.easysoa.registry.rest.client.types.ServiceInformation;
import org.easysoa.registry.rest.client.types.java.MavenDeliverableInformation;
import org.easysoa.registry.rest.marshalling.SoaNodeInformation;
import org.easysoa.registry.types.ServiceImplementation;

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

    public Collection<SoaNodeInformation> handleSources(JavaSource[] sources, 
            MavenDeliverableInformation mavenDeliverable, Log log) throws Exception {
        List<SoaNodeInformation> discoveredNodes = new LinkedList<SoaNodeInformation>();

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
                        wsInjectableTypeSet.add(c.asType());
                        
                        // also in first pass for itf, Extract WS info
                        ServiceInformation serviceDef = new ServiceInformation(c.getName());
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
        
    public Collection<SoaNodeInformation> handleClass(JavaClass c, JavaSource[] sources, 
            MavenDeliverableInformation deliverable, Log log) throws Exception {
        List<SoaNodeInformation> discoveredNodes = new LinkedList<SoaNodeInformation>();
        
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
            ServiceImplInformation serviceImpl = new ServiceImplInformation(c.getName());
            serviceImpl.setTitle(c.getFullyQualifiedName());
            serviceImpl.setProperty(ServiceImplementation.XPATH_TECHNOLOGY, "JAX-RS");
            serviceImpl.setProperty(ServiceImplementation.XPATH_DOCUMENTATION, c.getComment());
            serviceImpl.addParentDocument(deliverable.getSoaNodeId());
            discoveredNodes.add(serviceImpl);
            
            if (itf != null) {
                // Extract WS info
                ServiceInformation serviceDef = new ServiceInformation(itf.getName());
                serviceImpl.addParentDocument(serviceDef.getSoaNodeId());
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
                    serviceImpl.setProperty(ServiceImplementation.XPATH_OPERATIONS, operationInfoString);
                }
            }
        }

        // member injected by service (Path) annotated interfaces :
        handleInjectedMembers(c, deliverable, log);
        
        return discoveredNodes;
        
    }

}
