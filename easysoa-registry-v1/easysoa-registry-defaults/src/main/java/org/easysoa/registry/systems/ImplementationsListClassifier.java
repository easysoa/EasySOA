package org.easysoa.registry.systems;

import java.util.Map;

import org.easysoa.registry.types.ServiceImplementation;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.schema.types.Type;

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
        if (!isInTypeArray(ServiceImplementation.DOCTYPE, model.getDocumentType().getTypeHierarchy())) {
            return null;
        }
        return "/";
    }
    
    private boolean isInTypeArray(String typeToSearch, Type[] types) {
        for (Type type : types) {
            if (type.getName().equals(typeToSearch)) {
                return true;
            }
        }
        return false;
    }
 
}
