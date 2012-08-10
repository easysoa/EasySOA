package org.easysoa.registry.systems;

import java.util.Map;

import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.DocumentModel;

public class ByAlphabeticalOrder implements IntelligentSystemTreeClassifier {
    
    public static final String DEFAULT_ENVIRONMENT = "Unspecified";
    
    private int depth = 1;
    
    @Override
    public void initialize(Map<String, String> params) {
        if (params.containsKey("depth")) {
            depth = Integer.parseInt(params.get("depth"));
        }
    }

    @Override
    public String classify(DocumentModel model) throws ClientException {
        String title = model.getTitle();
        
        // NOTE: Even paths with leading and trailing slashes must work
        String classification = "/";
        for (int i = 0; i < depth; i++) {
            classification += title.charAt(i) + "/";
        }
        
        return classification.toUpperCase();
        
    }

}
