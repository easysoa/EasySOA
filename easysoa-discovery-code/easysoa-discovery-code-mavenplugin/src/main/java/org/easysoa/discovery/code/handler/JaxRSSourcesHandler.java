package org.easysoa.discovery.code.handler;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.maven.plugin.logging.Log;
import org.easysoa.discovery.code.CodeDiscoveryRegistryClient;
import org.easysoa.discovery.code.JavaServiceImplementationInformation;
import org.easysoa.discovery.code.ParsingUtils;
import org.easysoa.registry.rest.client.types.ServiceInformation;
import org.easysoa.registry.rest.client.types.java.MavenDeliverableInformation;
import org.easysoa.registry.rest.marshalling.SoaNodeInformation;
import org.easysoa.registry.types.OperationImplementation;
import org.easysoa.registry.types.java.JavaServiceImplementation;

import com.thoughtworks.qdox.model.JavaClass;
import com.thoughtworks.qdox.model.JavaMethod;
import com.thoughtworks.qdox.model.JavaSource;
import com.thoughtworks.qdox.model.Type;

/**
 * In project & current deliverable, reports :
 * * JAXRS service impl and their impl'd service interface if any (from impl and also by themselves),
 * both by having class or method(s) annotated by @Path
 * * thanks to InterfaceHandlerBase, member (field or bean setter)-injected service :
 * typed by interfaces having class or method(s) annotated by @Path
 */
public class JaxRSSourcesHandler extends AbstractJavaSourceHandler implements SourcesHandler {

    private static final String ANN_PATH = "javax.ws.rs.Path";
    private static final String[] ANN_METHODS = new String[] {
        "javax.ws.rs.GET", "javax.ws.rs.POST", "javax.ws.rs.PUT",
        "javax.ws.rs.HEAD", "javax.ws.rs.OPTIONS"
      };
    
    @Override
    public Collection<Type> findWSInterfaces(JavaSource[] sources,
            MavenDeliverableInformation mavenDeliverable, CodeDiscoveryRegistryClient registryClient, Log log)
            throws Exception {
        List<Type> wsInjectableTypeSet = new ArrayList<Type>();
        
        // Pass 1 : Find all WS interfaces if any
        for (JavaSource source : sources) {
            JavaClass[] classes = source.getClasses();
            for (JavaClass c : classes) {
                if (c.isInterface() && !SourcesParsingUtils.isTestClass(c)) {
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
                    }
                }
            }
        }
        return wsInjectableTypeSet;
    }
   
    @Override
    public Collection<SoaNodeInformation> findWSImplementations(JavaSource[] sources,
            Collection<Type> wsInterfaces, MavenDeliverableInformation mavenDeliverable,
            CodeDiscoveryRegistryClient registryClient, Log log) throws Exception {
        // Pass 2 : Find all WS impl, including those implementing known interfaces (though its not "classical" JAXRS)
        List<SoaNodeInformation> discoveredNodes = new ArrayList<SoaNodeInformation>();
        for (JavaSource source : sources) {
            // TODO diff between main & tests
            JavaClass[] classes = source.getClasses();
            for (JavaClass c : classes) {
                if (!c.isInterface()) {
                    discoveredNodes.addAll(this.handleClass(c, sources, wsInterfaces, mavenDeliverable, log));
                }
            }
        }
        return discoveredNodes;
    }

    public Collection<SoaNodeInformation> handleClass(JavaClass c, JavaSource[] sources, 
            Collection<Type> wsInterfaces, MavenDeliverableInformation deliverable, Log log) throws Exception {
        List<SoaNodeInformation> discoveredNodes = new ArrayList<SoaNodeInformation>();
        
        JavaClass itf = getWsItf(c, wsInterfaces);

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
            JavaServiceImplementationInformation serviceImpl = new JavaServiceImplementationInformation(c.getFullyQualifiedName());
            serviceImpl.setTitle(c.getName());
            serviceImpl.setProperty(JavaServiceImplementation.XPATH_TECHNOLOGY, "JAX-RS");
            serviceImpl.setProperty(JavaServiceImplementation.XPATH_DOCUMENTATION, c.getComment());
            serviceImpl.setProperty(JavaServiceImplementation.XPATH_ISMOCK, SourcesParsingUtils.isTestClass(c));
            serviceImpl.addParentDocument(deliverable.getSoaNodeId());
            
            if (itf != null) {
                // Extract WS info
                serviceImpl.setProperty(JavaServiceImplementation.XPATH_IMPLEMENTEDINTERFACE, itf.getFullyQualifiedName());
                ServiceInformation serviceDef = new ServiceInformation(itf.getName());
                serviceImpl.addParentDocument(serviceDef.getSoaNodeId());
                discoveredNodes.add(serviceDef);
            }
            
            // Extract operations info
            List<OperationImplementation> operations = serviceImpl.getOperations();
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
                        
                        operations.add(new OperationImplementation(
                        		method.getName(),
                        		null,
                        		"Method: " + httpMethod + ", Path: " + path + ", Description: " + method.getComment()));
                    }
                }
                serviceImpl.setOperations(operations);
                
                discoveredNodes.add(serviceImpl);
            }
        }
        
        return discoveredNodes;
        
    }

}
