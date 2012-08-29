package org.easysoa.registry.types;


public interface DeployedDeliverable extends SoaNode {

    public static final String DOCTYPE = "DeployedDeliverable";
    public static final String XPATH_ENVIRONMENT = "env:environment";

    String getEnvironment() throws Exception;

}