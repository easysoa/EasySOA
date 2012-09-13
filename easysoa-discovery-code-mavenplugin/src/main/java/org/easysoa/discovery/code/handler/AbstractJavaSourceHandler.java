package org.easysoa.discovery.code.handler;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import org.apache.maven.plugin.logging.Log;
import org.easysoa.discovery.code.CodeDiscoveryRegistryClient;
import org.easysoa.discovery.code.handler.consumption.AnnotatedServicesFinder;
import org.easysoa.discovery.code.handler.consumption.ImportedServicesFinder;
import org.easysoa.discovery.code.handler.consumption.ServiceConsumptionFinder;
import org.easysoa.registry.rest.client.types.java.MavenDeliverableInformation;
import org.easysoa.registry.rest.marshalling.SoaNodeInformation;

import com.thoughtworks.qdox.model.JavaClass;
import com.thoughtworks.qdox.model.JavaSource;
import com.thoughtworks.qdox.model.Type;


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

    private AnnotatedServicesFinder annotatedServicesFinder;

    protected AbstractJavaSourceHandler() {
        this.annotatedServicesFinder = new AnnotatedServicesFinder(null);
        this.annotatedServicesFinder.addAnnotationToDetect(ANN_INJECT); // Java 6
        this.annotatedServicesFinder.addAnnotationToDetect("org.osoa.sca.annotations.Reference");
        this.annotatedServicesFinder.addAnnotationToDetect("org.springframework.beans.factory.annotation.Autowired");
        this.annotatedServicesFinder.addAnnotationToDetect("com.google.inject.Inject");
        this.annotatedServicesFinder.addAnnotationToDetect("javax.ejb.EJB");
        this.serviceConsumptionFinders.add(this.annotatedServicesFinder);
        this.serviceConsumptionFinders.add(new ImportedServicesFinder());
    }
    
    protected void addAnnotationToDetect(String annotationToDetect) {
        annotatedServicesFinder.addAnnotationToDetect(annotationToDetect);
    }

    protected JavaClass getWsItf(JavaClass c, Collection<Type> serviceInterfaces) {
        for (JavaClass itfClass : c.getImplementedInterfaces()) {
            if (serviceInterfaces.contains(itfClass.asType())) {
                return itfClass;
            }
        }
        return null;
    }

    public Collection<SoaNodeInformation> handleSources(JavaSource[] sources,
            MavenDeliverableInformation mavenDeliverable,
            CodeDiscoveryRegistryClient registryClient, Log log) throws Exception {
        List<SoaNodeInformation> discoveredNodes = new LinkedList<SoaNodeInformation>();
        
        // Find WS interfaces
        Collection<Type> wsInterfaces = findWSInterfaces(sources,
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
        discoveredNodes.addAll(handleAdditionalDiscovery(sources, wsInterfaces, wsImpls,
                mavenDeliverable, registryClient, log));
        
        return discoveredNodes;
    }
    
    public abstract Collection<Type> findWSInterfaces(JavaSource[] sources,
            MavenDeliverableInformation mavenDeliverable,
            CodeDiscoveryRegistryClient registryClient, Log log) throws Exception;

    public abstract Collection<SoaNodeInformation> findWSImplementations(JavaSource[] sources,
            Collection<Type> wsInterfaces, MavenDeliverableInformation mavenDeliverable,
            CodeDiscoveryRegistryClient registryClient, Log log) throws Exception;

    public Collection<SoaNodeInformation> handleAdditionalDiscovery(JavaSource[] sources,
            Collection<Type> wsInterfaces, Collection<SoaNodeInformation> wsImpls, 
            MavenDeliverableInformation mavenDeliverable,
            CodeDiscoveryRegistryClient registryClient, Log log) throws Exception {
        return Collections.emptyList();
    }
}
