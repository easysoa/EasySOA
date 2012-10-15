package org.easysoa.discovery.code.handler;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.maven.plugin.logging.Log;
import org.easysoa.discovery.code.CodeDiscoveryRegistryClient;
import org.easysoa.discovery.code.ParsingUtils;
import org.easysoa.discovery.code.handler.consumption.ImportedServicesConsumptionFinder;
import org.easysoa.discovery.code.model.JavaServiceConsumptionInformation;
import org.easysoa.discovery.code.model.JavaServiceImplementationInformation;
import org.easysoa.discovery.code.model.JavaServiceInterfaceInformation;
import org.easysoa.registry.rest.client.types.ServiceInformation;
import org.easysoa.registry.rest.client.types.java.MavenDeliverableInformation;
import org.easysoa.registry.rest.marshalling.SoaNodeInformation;
import org.easysoa.registry.types.OperationImplementation;
import org.easysoa.registry.types.java.JavaServiceImplementation;

import com.thoughtworks.qdox.model.Annotation;
import com.thoughtworks.qdox.model.JavaClass;
import com.thoughtworks.qdox.model.JavaMethod;
import com.thoughtworks.qdox.model.JavaParameter;
import com.thoughtworks.qdox.model.JavaSource;
import com.thoughtworks.qdox.model.Type;


/**
 * In project & current deliverable, reports :
 * * JAXWS service impl and their impl'd service interface (from impl and also by themselves),
 * both by being annotated by @WebService
 * * thanks to InterfaceHandlerBase, member (field or bean setter)-injected service :
 * typed by @WebService annotated interfaces or @WebServiceClient annotated classes)
 * 
 * @author mdutoo
 *
 */
public class JaxWSSourcesHandler extends AbstractJavaSourceHandler implements SourcesHandler {

    private static final String ANN_WSPROVIDER = "javax.jws.WebServiceProvider";
    private static final String ANN_WS = "javax.jws.WebService";
    private static final String ANN_WEBRESULT = "javax.jws.WebResult";
    private static final String ANN_WEBPARAM = "javax.jws.WebParam";

    private static final String ANN_XML_WSCLIENT = "javax.xml.ws.WebServiceClient";
    private static final String ANN_XML_WSREF = "javax.xml.ws.WebServiceRef";
    private static final String ANN_XML_WSPROVIDER = "javax.xml.ws.WebServiceProvider";

    private static final String ANN_JUNIT_TEST = "org.junit.Test";
    
    private Map<Type, String> implsToInterfaces = new HashMap<Type, String>();
    
    public JaxWSSourcesHandler() {
        super();
        this.addAnnotationToDetect(ANN_WSPROVIDER);
        this.addAnnotationToDetect(ANN_XML_WSCLIENT);
        this.addAnnotationToDetect(ANN_XML_WSREF);
        this.addAnnotationToDetect(ANN_XML_WSPROVIDER);
    }
    
    @Override
    public Map<String, JavaServiceInterfaceInformation> findWSInterfaces(JavaSource source,
            MavenDeliverableInformation mavenDeliverable,
            CodeDiscoveryRegistryClient registryClient, Log log) throws Exception {
        // Pass 1 : Find all WS clients/interfaces
        Map<String, JavaServiceInterfaceInformation> wsInjectableTypeSet
                = new HashMap<String, JavaServiceInterfaceInformation>();
        
        JavaClass[] classes = source.getClasses();
        for (JavaClass c : classes) {
            boolean isWs = ParsingUtils.hasAnnotation(c, ANN_WS);
            boolean isInterface = c.isInterface();
            
            if (isWs && isInterface
                    || ParsingUtils.hasAnnotation(c, ANN_XML_WSCLIENT)
                    || ParsingUtils.hasAnnotation(c, ANN_WSPROVIDER)
                    || ParsingUtils.hasAnnotation(c, ANN_XML_WSPROVIDER)) {
                wsInjectableTypeSet.put(c.getFullyQualifiedName(), 
                        new JavaServiceInterfaceInformation(mavenDeliverable.getGroupId(),
                                mavenDeliverable.getArtifactId(),
                                c.getFullyQualifiedName()));
            }
        }

        return wsInjectableTypeSet;
    }

    @Override
    public JavaServiceInterfaceInformation findWSInterfaceInClasspath(Class<?> candidateClass,
            MavenDeliverableInformation mavenDeliverable, CodeDiscoveryRegistryClient registryClient, Log log)
            throws Exception {
        boolean isWs = ParsingUtils.hasAnnotation(candidateClass, ANN_WS);
        boolean isInterface = candidateClass.isInterface();
        
        if (isWs && isInterface
                || ParsingUtils.hasAnnotation(candidateClass, ANN_XML_WSCLIENT)
                || ParsingUtils.hasAnnotation(candidateClass, ANN_WSPROVIDER)
                || ParsingUtils.hasAnnotation(candidateClass, ANN_XML_WSPROVIDER)) {
            return new JavaServiceInterfaceInformation(mavenDeliverable.getGroupId(),
                            mavenDeliverable.getArtifactId(),
                            candidateClass.getName());
        }
        else {
            return null;
        }
    }

    @Override
    public Collection<SoaNodeInformation> findWSImplementations(JavaSource[] sources,
            Map<String, JavaServiceInterfaceInformation> wsInterfaces,
            MavenDeliverableInformation mavenDeliverable, CodeDiscoveryRegistryClient registryClient, Log log)
            throws Exception {
        // Pass 2 : Explore each impl
        List<SoaNodeInformation> discoveredNodes = new ArrayList<SoaNodeInformation>();
        for (JavaSource source : sources) {
            JavaClass[] classes = source.getClasses();
            for (JavaClass c : classes) {
                // Check JAX-WS annotation 
                JavaClass itfClass = getWsItf(c, wsInterfaces); // TODO several interfaces ???
                if (!c.isInterface() && (ParsingUtils.hasAnnotation(c, ANN_WS) || itfClass != null)) { // TODO superclass ?
                    // Extract interface info
                    //System.out.println("\ncp:\n" + System.getProperty("java.class.path"));
                    if (itfClass != null) {
                        implsToInterfaces.put(c.asType(), itfClass.asType().getFullyQualifiedName());
                    }
                    else {
                        log.warn("Couldn't find interface for class " + c.getFullyQualifiedName());
                    }
                    
                    // Extract WS info
                    JavaServiceImplementationInformation serviceImpl = new JavaServiceImplementationInformation(
                            c.getFullyQualifiedName());
                    serviceImpl.setTitle(c.getName());
                    serviceImpl.setProperty(JavaServiceImplementation.XPATH_TECHNOLOGY, "JAX-WS");
                    serviceImpl.setProperty(JavaServiceImplementation.XPATH_ISMOCK,
                            c.getSource().getURL().getPath().contains("src/test/"));
                    serviceImpl.setProperty(JavaServiceImplementation.XPATH_IMPLEMENTATIONCLASS, c.getFullyQualifiedName());
                    if (itfClass != null) {
                        serviceImpl.setProperty(JavaServiceImplementation.XPATH_IMPLEMENTEDINTERFACE, itfClass.getFullyQualifiedName());
                        JavaServiceInterfaceInformation interfaceInfo = wsInterfaces.get(itfClass.getFullyQualifiedName());
                        if (interfaceInfo != null) {
                            serviceImpl.setProperty(JavaServiceImplementation.XPATH_IMPLEMENTEDINTERFACELOCATION,
                                    interfaceInfo.getMavenDeliverableId().getName());
                        }
                    }
                    serviceImpl.addParentDocument(mavenDeliverable.getSoaNodeId());
                    discoveredNodes.add(serviceImpl);
                    
                    if (itfClass != null) {
                        // Extract service info
                        String itfClassName = itfClass.getName();
                        ServiceInformation serviceDef = new ServiceInformation(
                                itfClassName.substring(itfClassName.lastIndexOf(".") + 1));
                        serviceImpl.addParentDocument(serviceDef.getSoaNodeId());
                            serviceImpl.setProperty(JavaServiceImplementation.XPATH_DOCUMENTATION, itfClass.getComment());
                        discoveredNodes.add(serviceDef);
                        
                        // Extract operations info
                        List<OperationImplementation> operations = serviceImpl.getOperations();
                        for (JavaMethod method : itfClass.getMethods()) {
                            if (ParsingUtils.hasAnnotation(method, ANN_WEBRESULT)) {
                                Annotation webResultAnn = ParsingUtils.getAnnotation(method, ANN_WEBRESULT);
                                
                                // Extract parameters info
                                StringBuilder parametersInfo = new StringBuilder();
                                for (JavaParameter parameter : method.getParameters()) {
                                    Annotation webParamAnn = ParsingUtils.getAnnotation(parameter, ANN_WEBPARAM);
                                    parametersInfo.append(webParamAnn.getProperty("name").getParameterValue()
                                            + "=" + parameter.getType().toString() + ", ");
                                }
                                operations.add(new OperationImplementation(
                                        webResultAnn.getProperty("name").toString(),
                                        parametersInfo.delete(parametersInfo.length()-2, parametersInfo.length()).toString(),
                                        method.getComment()));
                            }
                        }
                        serviceImpl.setOperations(operations);
                    }
                }
            }
        }
        return discoveredNodes;
    }
   
    
     @Override
    public Collection<SoaNodeInformation> handleAdditionalDiscovery(JavaSource[] sources,
            Map<String, JavaServiceInterfaceInformation> wsInterfaces,
            MavenDeliverableInformation mavenDeliverable, CodeDiscoveryRegistryClient registryClient, Log log)
            throws Exception {
         List<SoaNodeInformation> discoveredNodes = new ArrayList<SoaNodeInformation>();
         
        // Additional pass : Find WS tests
        for (JavaSource source : sources) {
            JavaClass[] classes = source.getClasses();
            for (JavaClass c : classes) {
                if (!c.isInterface() && c.getSource().getURL().getPath().contains("src/test/")) {
                	boolean isUnitTestingClass = false;
                	for (JavaMethod method : c.getMethods()) {
                		if (ParsingUtils.hasAnnotation(method, ANN_JUNIT_TEST)) {
                			isUnitTestingClass = true;
                			break;
                		}
                	}
                	if (isUnitTestingClass) {
                	    ImportedServicesConsumptionFinder importedServiceFinders = new ImportedServicesConsumptionFinder();
                	    List<JavaServiceConsumptionInformation> foundConsumptions = importedServiceFinders.find(
                	            c, mavenDeliverable, wsInterfaces);
                	    for (JavaServiceConsumptionInformation foundConsumption : foundConsumptions) {
                            // Try to attach test to existing non-mock impls
                            boolean foundOriginalImplementation = false;
                            SoaNodeInformation[] matchingRegistryImpls = registryClient
                                    .findImplsByInterface(foundConsumption.getConsumedInterface());
                            for (SoaNodeInformation matchingRegistryImpl : matchingRegistryImpls) {
                                foundOriginalImplementation = true;
                                discoveredNodes.add(createTestDiscovery(
                                        matchingRegistryImpl.getSoaName(),
                                        c.getFullyQualifiedName()));
                            }
                            
                            // Otherwise, attach test info to all known implementations of the interface
                            if (!foundOriginalImplementation) {
                                for (Entry<Type, String> implToInterface : implsToInterfaces.entrySet()) {
                                    if (foundConsumption.getConsumedInterface().equals(implToInterface)) {
                                        discoveredNodes.add(createTestDiscovery(
                                                implToInterface.getKey().toGenericString(),
                                                c.getFullyQualifiedName()));
                                    }
                                }
                            }
                	    }
                	}
                }
            }
        }
        
        return discoveredNodes;
    }
    
    public JavaServiceImplementationInformation createTestDiscovery(String serviceImplName, String testName) throws Exception {
        JavaServiceImplementationInformation serviceImpl = new JavaServiceImplementationInformation(serviceImplName);
        List<String> tests = new ArrayList<String>();
        tests.add(testName);
        serviceImpl.setTests(tests);
        return serviceImpl;
    }
    
}
