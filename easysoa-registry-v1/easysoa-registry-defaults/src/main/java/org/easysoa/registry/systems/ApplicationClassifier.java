package org.easysoa.registry.systems;

import java.util.Map;

import org.easysoa.registry.types.Deliverable;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.DocumentModel;

/**
 * System hierachy made of deliverables classified by related application.
 * 
 * @author mkalam-alami
 *
 */
public class ApplicationClassifier implements IntelligentSystemTreeClassifier {

    @Override
    public void initialize(Map<String, String> params) {
        // No parameters
    }

    @Override
    public String classify(DocumentModel model) throws ClientException {
        // Filter non-Maven documents
        if (!Deliverable.DOCTYPE.equals(model.getType())) {
            return null;
        }
        
        // Return application name
        Deliverable deliverable = model.getAdapter(Deliverable.class);
        return deliverable.getApplication();
    }

}
