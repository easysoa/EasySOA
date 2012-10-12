package org.easysoa.discovery.code.handler;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.maven.plugin.logging.Log;
import org.easysoa.discovery.code.CodeDiscoveryMojo;
import org.easysoa.discovery.code.CodeDiscoveryRegistryClient;
import org.easysoa.discovery.code.handler.consumption.AnnotatedServicesConsumptionFinder;
import org.easysoa.discovery.code.handler.consumption.ImportedServicesConsumptionFinder;
import org.easysoa.discovery.code.handler.consumption.ServiceConsumptionFinder;
import org.easysoa.discovery.code.model.JavaServiceInterfaceInformation;
import org.easysoa.registry.rest.client.types.java.MavenDeliverableInformation;
import org.easysoa.registry.rest.marshalling.SoaNodeInformation;

import com.thoughtworks.qdox.model.JavaClass;
import com.thoughtworks.qdox.model.JavaSource;


/**
 * Provides Java-generic discovery features for interfaces, for now only
 * detection of Java interface-typed injected (through various methods) members.
 * 
 * This provides only potential, coarse grain (among a set of deployed deliverables,
 * any service provider implementation MAY use any service consumer implementation) &
 * partial (services may also be consumed after being looked up in a registry, not
 * only through member injection) dependencies between services.
 * 
 * These dependencies should be refined by providing EasySOA with explicit
 * architecture information about architectural components (classified deliverable
 * may already be a good guide there) and business processes. 
 * 
 * LATER Thoughts about more injection strategies :
 * recursive (including native Java services being injected in one another, but adds complexity),
 * imports (but qdox can't), non-null assigned variable (but requires almost being runtime)
 * 
 * @author mdutoo
 *
 */
public abstract class AbstractJavaSourceHandler implements SourcesHandler {
    
    private static final String ANN_INJECT = "javax.inject.Inject";

    private List<ServiceConsumptionFinder> serviceConsumptionFinders = new ArrayList<ServiceConsumptionFinder>();

    private AnnotatedServicesConsumptionFinder annotatedServicesFinder;

    protected AbstractJavaSourceHandler() {
        this.annotatedServicesFinder = new AnnotatedServicesConsumptionFinder(null);
        this.annotatedServicesFinder.addAnnotationToDetect(ANN_INJECT); // Java 6
        this.annotatedServicesFinder.addAnnotationToDetect("org.osoa.sca.annotations.Reference");
        this.annotatedServicesFinder.addAnnotationToDetect("org.springframework.beans.factory.annotation.Autowired");
        this.annotatedServicesFinder.addAnnotationToDetect("com.google.inject.Inject");
        this.annotatedServicesFinder.addAnnotationToDetect("javax.ejb.EJB");
        this.serviceConsumptionFinders.add(this.annotatedServicesFinder);
        this.serviceConsumptionFinders.add(new ImportedServicesConsumptionFinder());
    }
    
    protected void addAnnotationToDetect(String annotationToDetect) {
        annotatedServicesFinder.addAnnotationToDetect(annotationToDetect);
    }

    protected JavaClass getWsItf(JavaClass c, Map<String, JavaServiceInterfaceInformation> serviceInterfaces) {
        for (JavaClass itfClass : c.getImplementedInterfaces()) {
            if (serviceInterfaces.containsKey(itfClass.getFullyQualifiedName())) {
                return itfClass;
            }
        }
        return null;
    }

    public Collection<SoaNodeInformation> handleSources(CodeDiscoveryMojo codeDiscovery,
            JavaSource[] sources,
            MavenDeliverableInformation mavenDeliverable,
            CodeDiscoveryRegistryClient registryClient, Log log) throws Exception {
        List<SoaNodeInformation> discoveredNodes = new LinkedList<SoaNodeInformation>();
        
        // Find WS interfaces
        Map<String, JavaServiceInterfaceInformation> wsInterfaces = findWSInterfaces(codeDiscovery, sources,
                mavenDeliverable, registryClient, log);
        
        // Find WS implementations
        Collection<SoaNodeInformation> wsImpls = findWSImplementations(sources,
                wsInterfaces, mavenDeliverable, registryClient, log);
        discoveredNodes.addAll(wsImpls);
        
        // Find WS consumptions 
        for (JavaSource source : sources) {
            for (ServiceConsumptionFinder serviceConsumptionFinder : this.serviceConsumptionFinders) {
                discoveredNodes.addAll(serviceConsumptionFinder.find(source, mavenDeliverable, wsInterfaces));
            }
        }
        
        // Additional discovery
        discoveredNodes.addAll(handleAdditionalDiscovery(sources, wsInterfaces,
                mavenDeliverable, registryClient, log));
        
        return discoveredNodes;
    }
    
    public abstract Map<String, JavaServiceInterfaceInformation> findWSInterfaces(CodeDiscoveryMojo codeDiscovery, JavaSource[] sources,
            MavenDeliverableInformation mavenDeliverable,
            CodeDiscoveryRegistryClient registryClient, Log log) throws Exception;

    public abstract Collection<SoaNodeInformation> findWSImplementations(JavaSource[] sources,
            Map<String, JavaServiceInterfaceInformation> wsInterfaces, MavenDeliverableInformation mavenDeliverable,
            CodeDiscoveryRegistryClient registryClient, Log log) throws Exception;

    public Collection<SoaNodeInformation> handleAdditionalDiscovery(JavaSource[] sources,
            Map<String, JavaServiceInterfaceInformation> wsInterfaces,
            MavenDeliverableInformation mavenDeliverable,
            CodeDiscoveryRegistryClient registryClient, Log log) throws Exception {
        return Collections.emptyList();
    }
}
