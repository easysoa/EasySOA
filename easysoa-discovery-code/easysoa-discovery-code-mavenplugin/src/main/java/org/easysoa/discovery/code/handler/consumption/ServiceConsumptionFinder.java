package org.easysoa.discovery.code.handler.consumption;

import java.util.Collection;
import java.util.List;

import org.easysoa.discovery.code.JavaServiceConsumptionInformation;
import org.easysoa.registry.types.java.MavenDeliverable;

import com.thoughtworks.qdox.model.JavaSource;
import com.thoughtworks.qdox.model.Type;

public interface ServiceConsumptionFinder {

    List<JavaServiceConsumptionInformation> find(JavaSource javaSource,
            MavenDeliverable mavenDeliverable, Collection<Type> serviceInterfaces) throws Exception;
    
}
