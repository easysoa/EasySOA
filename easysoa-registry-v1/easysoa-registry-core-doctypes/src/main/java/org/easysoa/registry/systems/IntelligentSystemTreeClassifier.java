package org.easysoa.registry.systems;

import java.util.Map;

import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.DocumentModel;

public interface IntelligentSystemTreeClassifier {
    
    void initialize(Map<String, String> params);
    
    /**
     * Determines if the given document model should be stored within this intelligent system tree,
     * and if so at which path it should be stored.
     * 
     * If the model is accepted by the system tree, it returns the path of the system where
     * the document should be stored, relative to the system tree root. Examples:
     * 
     * "MySystem" : The document should be stored in the MySystem system
     * "MySystem/MyChildSystem" : The document should be stored in the MyChildSystem system, itself stored in MySystem
     * null : The document should not be stored in the system tree
     * 
     * @param model the model to test, never null
     * @return the expected path, or null
     * @throws ClientException 
     */
    // TODO Give more flexibility to allow for setting System title, possibly more properties
    String classify(DocumentModel model) throws ClientException;
    
}
