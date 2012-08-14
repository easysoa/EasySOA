package org.easysoa.registry.types;

import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.model.PropertyException;

public interface DeployedDeliverable extends Document {

    public static final String DOCTYPE = "DeployedDeliverable";
    public static final String XPATH_ENVIRONMENT = "env:environment";

    String getDoctype();

    String getEnvironment() throws PropertyException, ClientException;

}