package org.easysoa.validation;

import java.util.List;

import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;

/**
 * 
 * Class to be implemented by each service validator contributed to the ServiceValidatorComponent.
 * 
 * @author mkalam-alami
 *
 */
public abstract class ServiceValidator {
    
    private String name = null;
    
    private String label = null;
    
    /**
     * Runs a validation of a service against a referenced service.
     * Contract: Both models are guaranteed not to be null
     * @param session
     * @param service
     * @param referenceService
     * @return A list of errors OR an empty list if no error
     * @throws ClientException Any exception that prevents the validation to be run
     */
    public abstract List<String> validateService(CoreSession session, DocumentModel service, DocumentModel referenceService) throws Exception;

    public String getName() {
        return this.name;
    }
    
    public void setName(String name) {
        this.name = name;
    }

    public Object getLabel() {
        return this.label;
    }
    
    public void setLabel(String label) {
        this.label = label;
    }


}
