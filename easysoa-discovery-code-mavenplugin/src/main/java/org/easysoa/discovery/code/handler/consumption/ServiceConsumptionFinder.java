package org.easysoa.discovery.code.handler.consumption;

import java.util.List;
import java.util.Map;

import org.easysoa.discovery.code.model.JavaServiceConsumptionInformation;
import org.easysoa.discovery.code.model.JavaServiceInterfaceInformation;
import org.easysoa.registry.types.java.MavenDeliverable;

import com.thoughtworks.qdox.model.JavaSource;

public interface ServiceConsumptionFinder {

    List<JavaServiceConsumptionInformation> find(JavaSource javaSource,
            MavenDeliverable mavenDeliverable,
            Map<String, JavaServiceInterfaceInformation> wsInterfaces) throws Exception;
    
}
