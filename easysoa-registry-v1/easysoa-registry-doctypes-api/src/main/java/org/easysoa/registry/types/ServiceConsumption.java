package org.easysoa.registry.types;

import java.util.List;

import org.easysoa.registry.SoaNodeId;



/**
 * 
 * @author mkalam-alami
 *
 */
public interface ServiceConsumption extends SoaNode {

    static final String DOCTYPE = "ServiceConsumption";
    
    public List<SoaNodeId> getConsumableServiceImpls() throws Exception;
    
}
