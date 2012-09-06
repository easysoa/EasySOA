package org.easysoa.registry.types;

import org.easysoa.registry.SoaNodeId;



/**
 * 
 * @author mkalam-alami
 *
 */
public interface EndpointConsumption extends ServiceConsumption {

    static final String DOCTYPE = "EndpointConsumption";
    
    static final String PREDICATE_CONSUMES = "consumes";
    
    SoaNodeId getConsumedEndpoint() throws Exception;
    
    void setConsumedEndpoint(SoaNodeId consumedEndpoint) throws Exception;
    
}
