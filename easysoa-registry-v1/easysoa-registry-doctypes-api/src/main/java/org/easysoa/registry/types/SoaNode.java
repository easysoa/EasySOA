package org.easysoa.registry.types;

import org.easysoa.registry.SoaNodeId;


public interface SoaNode extends Document {

    public static final String DOCTYPE = "SoaNode";
    
    public static final String FACET = "SoaNode";

    public static final String XPATH_SOANAME = "soan:name";

    SoaNodeId getSoaNodeId() throws Exception;

    String getSoaName() throws Exception;

}