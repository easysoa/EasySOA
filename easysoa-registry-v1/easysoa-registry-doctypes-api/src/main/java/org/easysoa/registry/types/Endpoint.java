package org.easysoa.registry.types;

public interface Endpoint extends SoaNode {

    public static final String DOCTYPE = "Endpoint";
    
    public static final String XPATH_ENVIRONMENT = "env:environment";

    String getDoctype();

    String getEnvironment() throws Exception;

}