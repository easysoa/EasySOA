package org.easysoa.registry.systems;

import java.util.Map;

import org.easysoa.registry.types.ServiceImplementation;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.DocumentModel;

/**
 * Flat system tree of all service implementations.
 * 
 * @author mkalam-alami
 *
 */
public class ImplementationsListClassifier implements IntelligentSystemTreeClassifier {

    public static final String DEFAULT_ENVIRONMENT = "Unspecified";
    
    @Override
    public void initialize(Map<String, String> params) {
        // No parameters
    }

    @Override
    public String classify(DocumentModel model) throws ClientException {
        if (!ServiceImplementation.DOCTYPE.equals(model.getType())) {
            return null;
        }
        return "/";
    }

}
