package org.easysoa.registry.types;


public interface DeployedDeliverable extends Document {

    public static final String DOCTYPE = "DeployedDeliverable";
    public static final String XPATH_ENVIRONMENT = "env:environment";

    String getDoctype();

    String getEnvironment() throws Exception;

}