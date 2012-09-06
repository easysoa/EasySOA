package org.easysoa.registry.types.java;

import org.easysoa.registry.types.ServiceConsumption;


public interface JavaServiceConsumption extends ServiceConsumption {

    static final String DOCTYPE = "JavaServiceConsumption";
    
    static final String XPATH_CONSUMEDINTERFACE = "javasc:consumedInterface";
    
    String getConsumedInterface() throws Exception;

}