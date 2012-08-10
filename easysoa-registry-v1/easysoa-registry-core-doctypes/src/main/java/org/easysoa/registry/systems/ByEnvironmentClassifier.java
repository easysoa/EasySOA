package org.easysoa.registry.systems;

import java.util.Map;

import org.easysoa.registry.types.DeployedDeliverableDoctype;
import org.easysoa.registry.types.EndpointDoctype;
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
        if (!EndpointDoctype.DOCTYPE.equals(model.getType())
                && !DeployedDeliverableDoctype.DOCTYPE.equals(model.getType())) {
            return null;
        }
        
        String environment = (String) model.getPropertyValue(EndpointDoctype.XPATH_ENVIRONMENT);
        if (environment == null) {
            return DEFAULT_ENVIRONMENT;
        }
        else {
            return environment;
        }
        
    }

}
