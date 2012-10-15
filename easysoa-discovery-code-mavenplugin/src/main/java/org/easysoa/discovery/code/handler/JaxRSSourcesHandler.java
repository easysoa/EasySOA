package org.easysoa.discovery.code.handler;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.maven.plugin.logging.Log;
import org.easysoa.discovery.code.CodeDiscoveryRegistryClient;
import org.easysoa.discovery.code.ParsingUtils;
import org.easysoa.discovery.code.model.JavaServiceImplementationInformation;
import org.easysoa.discovery.code.model.JavaServiceInterfaceInformation;
import org.easysoa.registry.rest.client.types.ServiceInformation;
import org.easysoa.registry.rest.client.types.java.MavenDeliverableInformation;
import org.easysoa.registry.rest.marshalling.SoaNodeInformation;
import org.easysoa.registry.types.OperationImplementation;
import org.easysoa.registry.types.java.JavaServiceImplementation;

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
public class JaxRSSourcesHandler extends AbstractJavaSourceHandler implements SourcesHandler {

    private static final String ANN_PATH = "javax.ws.rs.Path";
    private static final String[] ANN_METHODS = new String[] {
        "javax.ws.rs.GET", "javax.ws.rs.POST", "javax.ws.rs.PUT",
        "javax.ws.rs.HEAD", "javax.ws.rs.OPTIONS"
      };
    
    @Override
    public Map<String, JavaServiceInterfaceInformation> findWSInterfaces(JavaSource source,
            MavenDeliverableInformation mavenDeliverable, CodeDiscoveryRegistryClient registryClient,
            Log log) throws Exception {
        Map<String, JavaServiceInterfaceInformation> wsInjectableTypeSet = new HashMap<String, JavaServiceInterfaceInformation>();
        
        // Pass 1 : Find all WS interfaces if any
        JavaClass[] classes = source.getClasses();
        for (JavaClass c : classes) {
            if (c.isInterface() && !ParsingUtils.isTestClass(c)) {
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
                      wsInjectableTypeSet.put(c.getFullyQualifiedName(), new JavaServiceInterfaceInformation(
                              mavenDeliverable.getGroupId(), mavenDeliverable.getArtifactId(),
                              c.getFullyQualifiedName()));
                }
            }
        }
        
        return wsInjectableTypeSet;
    }
   
    @Override
    public JavaServiceInterfaceInformation findWSInterfaceInClasspath(Class<?> candidateClass,
            MavenDeliverableInformation mavenDeliverable, CodeDiscoveryRegistryClient registryClient, Log log)
            throws Exception {
        boolean hasPathMethods = false;
        for (Method method : candidateClass.getMethods()) {
            if (ParsingUtils.hasAnnotation(method, ANN_PATH)) {
                hasPathMethods = true;
            }
        }
        if (hasPathMethods || ParsingUtils.hasAnnotation(candidateClass, ANN_PATH)) {
             return new JavaServiceInterfaceInformation(
                      mavenDeliverable.getGroupId(), mavenDeliverable.getArtifactId(),
                      candidateClass.getName());
        }
        else {
            return null;
        }
    }

    @Override
    public Collection<SoaNodeInformation> findWSImplementations(JavaSource[] sources,
            Map<String, JavaServiceInterfaceInformation> wsInterfaces, MavenDeliverableInformation mavenDeliverable,
            CodeDiscoveryRegistryClient registryClient, Log log) throws Exception {
        // Pass 2 : Find all WS impl, including those implementing known interfaces (though its not "classical" JAXRS)
        List<SoaNodeInformation> discoveredNodes = new ArrayList<SoaNodeInformation>();
        for (JavaSource source : sources) {
            // TODO diff between main & tests
            JavaClass[] classes = source.getClasses();
            for (JavaClass c : classes) {
                if (!c.isInterface()) {
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
                        serviceImpl.setProperty(JavaServiceImplementation.XPATH_ISMOCK, ParsingUtils.isTestClass(c));
                        serviceImpl.setProperty(JavaServiceImplementation.XPATH_IMPLEMENTATIONCLASS, c.getFullyQualifiedName());
                        serviceImpl.addParentDocument(mavenDeliverable.getSoaNodeId());
                        
                        if (itf != null) {
                            // Extract WS info
                            serviceImpl.setProperty(JavaServiceImplementation.XPATH_IMPLEMENTEDINTERFACE, itf.getFullyQualifiedName());
                            JavaServiceInterfaceInformation interfaceInfo = wsInterfaces.get(itf.getFullyQualifiedName());
                            if (interfaceInfo != null) {
                                serviceImpl.setProperty(JavaServiceImplementation.XPATH_IMPLEMENTEDINTERFACELOCATION, interfaceInfo.getMavenDeliverableId().getName());
                            }
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
                }
            }
        }
        return discoveredNodes;
    }
    
}
