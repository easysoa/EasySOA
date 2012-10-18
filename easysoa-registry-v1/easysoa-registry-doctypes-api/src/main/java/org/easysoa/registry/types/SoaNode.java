package org.easysoa.registry.types;

import java.util.List;

import org.easysoa.registry.SoaNodeId;


public interface SoaNode extends Document {

	public static final String ABSTRACT_DOCTYPE = "SoaNode";
	
    public static final String SCHEMA = "soanode";
    
    public static final String XPATH_SOANAME = "soan:name";
    
    public static final String XPATH_ISPLACEHOLDER = "soan:isplaceholder";

    public static final String XPATH_PARENTSIDS = "soan:parentIds";
    
    SoaNodeId getSoaNodeId() throws Exception;

    String getSoaName() throws Exception;

    boolean isPlaceholder() throws Exception;
    
    void setIsPlaceholder(boolean isPlaceholder) throws Exception;

    List<SoaNodeId> getParentIds() throws Exception;

	void setParentIds(List<SoaNodeId> parentIds) throws Exception;
    
}