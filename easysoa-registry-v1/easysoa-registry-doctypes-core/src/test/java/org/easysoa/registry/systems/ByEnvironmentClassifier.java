package org.easysoa.registry.systems;

import java.util.Map;

import org.easysoa.registry.types.DeployedDeliverable;
import org.easysoa.registry.types.Endpoint;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.DocumentModel;

public class ByEnvironmentClassifier implements IntelligentSystemTreeClassifier {

    public static final String DEFAULT_ENVIRONMENT = "Unspecified";
    
    @Override
    public void initialize(Map<String, String> params) {
        // No parameters
    }

    @Override
    public String classify(DocumentModel model) throws ClientException {
        if (!Endpoint.DOCTYPE.equals(model.getType())
                && !DeployedDeliverable.DOCTYPE.equals(model.getType())) {
            return null;
        }
        
        String environment = (String) model.getPropertyValue(Endpoint.XPATH_ENVIRONMENT);
        if (environment == null) {
            return DEFAULT_ENVIRONMENT;
        }
        else {
            return environment;
        }
        
    }

}
