package org.easysoa.registry.systems;

import java.util.Map;

import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.DocumentModel;

public class EverythingFlatClassifier implements IntelligentSystemTreeClassifier {
    
    @Override
    public void initialize(Map<String, String> params) {
        // Nothing
    }

    @Override
    public String classify(DocumentModel model) throws ClientException {
        return "";
    }

}
