package org.easysoa.discovery.code.handler.consumption;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.easysoa.discovery.code.JavaServiceConsumptionInformation;
import org.easysoa.discovery.code.handler.SourcesParsingUtils;
import org.easysoa.registry.types.java.MavenDeliverable;

import com.thoughtworks.qdox.model.JavaClass;
import com.thoughtworks.qdox.model.JavaSource;
import com.thoughtworks.qdox.model.Type;

public class ImportedServicesFinder implements ServiceConsumptionFinder {

    private boolean filterSources = true;
    
    public ImportedServicesFinder() {
        
    }
    
    /**
     * Default value: true
     * @param filterSources
     */
    public void setFilterSources(boolean filterSources) {
        this.filterSources = filterSources;
    }
    
    @Override
    public List<JavaServiceConsumptionInformation> find(JavaSource javaSource, MavenDeliverable mavenDeliverable,
            Collection<Type> serviceInterfaces) throws Exception {
        List<JavaServiceConsumptionInformation> foundConsumptions = new ArrayList<JavaServiceConsumptionInformation>();
        for (JavaClass c : javaSource.getClasses()) {
            if (!filterSources || !SourcesParsingUtils.isTestClass(c)) {
                foundConsumptions.addAll(find(c, mavenDeliverable, serviceInterfaces));
            }
        }
        return foundConsumptions;
    }

    public List<JavaServiceConsumptionInformation> find(JavaClass c, MavenDeliverable mavenDeliverable,
            Collection<Type> serviceInterfaces) throws Exception {
        List<JavaServiceConsumptionInformation> foundConsumptions = new ArrayList<JavaServiceConsumptionInformation>();
        
        // Explore imports
        for (String importedClass : c.getSource().getImports()) {
            if (serviceInterfaces.contains(new Type(importedClass))) {
                foundConsumptions.add(new JavaServiceConsumptionInformation(
                        mavenDeliverable.getSoaNodeId(), importedClass));
            }
        }
        
        // TODO Also explore package
        
        return foundConsumptions;
    }

}
