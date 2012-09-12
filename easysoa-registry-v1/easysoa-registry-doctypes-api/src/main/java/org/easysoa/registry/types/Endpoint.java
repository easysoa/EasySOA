package org.easysoa.registry.types;

public interface Endpoint extends SoaNode {

    public static final String DOCTYPE = "Endpoint";
    
    public static final String XPATH_ENVIRONMENT = "env:environment";
    
    public static final String XPATH_URL = "endp:url";

    String getEnvironment() throws Exception;

}