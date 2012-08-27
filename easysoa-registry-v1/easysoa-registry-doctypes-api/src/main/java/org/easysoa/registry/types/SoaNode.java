package org.easysoa.registry.types;

import org.easysoa.registry.SoaNodeId;


public interface SoaNode extends Document {
    
    public static final String FACET = "SoaNode";

    public static final String XPATH_SOANAME = "soa:name";

    SoaNodeId getSoaNodeId();

    String getSoaName();

}