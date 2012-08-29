package org.easysoa.registry.types;

import org.easysoa.registry.SoaNodeId;


public interface SoaNode extends Document {
    
    public static final String FACET = "SoaNode";

    public static final String XPATH_SOANAME = "soan:name";

    String getDoctype() throws Exception;
    
    SoaNodeId getSoaNodeId() throws Exception;

    String getSoaName() throws Exception;

}