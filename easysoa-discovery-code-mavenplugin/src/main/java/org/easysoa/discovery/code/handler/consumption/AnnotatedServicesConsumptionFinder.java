package org.easysoa.discovery.code.handler.consumption;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.easysoa.discovery.code.ParsingUtils;
import org.easysoa.discovery.code.handler.SourcesParsingUtils;
import org.easysoa.discovery.code.model.JavaServiceConsumptionInformation;
import org.easysoa.discovery.code.model.JavaServiceInterfaceInformation;
import org.easysoa.registry.types.java.MavenDeliverable;

import com.thoughtworks.qdox.model.AbstractJavaEntity;
import com.thoughtworks.qdox.model.BeanProperty;
import com.thoughtworks.qdox.model.JavaClass;
import com.thoughtworks.qdox.model.JavaField;
import com.thoughtworks.qdox.model.JavaMethod;
import com.thoughtworks.qdox.model.JavaParameter;
import com.thoughtworks.qdox.model.JavaSource;
import com.thoughtworks.qdox.model.Type;

public class AnnotatedServicesConsumptionFinder implements ServiceConsumptionFinder {

    // configuration :
    private boolean allInjected = true;

    private boolean filterSources = true;
    
    private List<String> annotationsToDetect;

    public AnnotatedServicesConsumptionFinder(List<String> annotationsToDetect) {
        this.annotationsToDetect = annotationsToDetect;
        if (this.annotationsToDetect == null) {
            this.annotationsToDetect = new ArrayList<String>();
        }
    }
    
    public void addAnnotationToDetect(String annotationToDetect) {
        annotationsToDetect.add(annotationToDetect);
    }

    
    /**
     * Default value: true
     * @param allInjected
     */
    public void setAllInjected(boolean allInjected) {
        this.allInjected = allInjected;
    }

    
    /**
     * Default value: true
     * @param filterSources
     */
    public void setFilterSources(boolean filterSources) {
        this.filterSources = filterSources;
    }
    
    @Override
    public List<JavaServiceConsumptionInformation> find(JavaSource javaSource,
            MavenDeliverable mavenDeliverable, Map<String, JavaServiceInterfaceInformation> serviceInterfaces) throws Exception {
        List<JavaServiceConsumptionInformation> discoveredConsumptions = new LinkedList<JavaServiceConsumptionInformation>();
        // NB. JAXWS WebServiceClient (generated client stub) not reported as such but through injection below
        // (though they could be, as "connector" TODO)
        // member injected by WebService annotated interfaces, WebServiceClients (generated client stub) or WebServiceRefs :
        
        for (JavaClass c : javaSource.getClasses()) {
            if (!filterSources || !SourcesParsingUtils.isTestClass(c)) {
                // Java 6 (and other methods) injection of fields by service-annotated interfaces 
                // in injected fields :
                HashSet<String> injectedBeanProperties = new HashSet<String>();
                for (JavaField javaField : c.getFields()) { // TODO also superfields...
                    addConsumerFoundInInjectedMember(discoveredConsumptions, javaField, javaField.getType(),
                            javaField.getName(), injectedBeanProperties, serviceInterfaces, mavenDeliverable);
                }
                // in injected setters :
                for (BeanProperty beanProperty : c.getBeanProperties()) {
                    JavaMethod method = beanProperty.getMutator();
                    if (method != null) {
                        JavaParameter[] parameters = method.getParameters();
                        if (parameters.length == 1) {
                            addConsumerFoundInInjectedMember(discoveredConsumptions, method, parameters[0].getType(),
                                    beanProperty.getName(), injectedBeanProperties, serviceInterfaces, mavenDeliverable);
                        }
                    }
                }
            }
        }
        
        return discoveredConsumptions;
    }

    private void addConsumerFoundInInjectedMember(List<JavaServiceConsumptionInformation> discoveredConsumptions,
            AbstractJavaEntity injectedMember, Type injectedType,
            String beanPropertyName, HashSet<String> injectedBeanProperties,
            Map<String, JavaServiceInterfaceInformation> serviceInterfaces,
            MavenDeliverable mavenDeliverable) throws Exception {
        if (injectedBeanProperties.contains(beanPropertyName)) {
            return;
        }
        String injectionAnnotation = getInjectionAnnotation(injectedMember);
        if (allInjected || injectionAnnotation != null) {
            if (serviceInterfaces.containsKey(injectedType.getFullyQualifiedName())) {
                discoveredConsumptions.add(new JavaServiceConsumptionInformation(
                        mavenDeliverable.getSoaNodeId(),
                        null, // TODO from class?
                        injectedType.toGenericString(),
                        injectedType.getJavaClass().getSource().getURL().toString()));
                injectedBeanProperties.add(beanPropertyName);
            }
        }
    }

    private String getInjectionAnnotation(AbstractJavaEntity javaInjectionMember) {
        for (String injectionAnnotation : this.annotationsToDetect) {
            if (ParsingUtils.hasAnnotation(javaInjectionMember, injectionAnnotation)) {
                return injectionAnnotation;
            }
        }
        return null;
    }
    
}
