package org.easysoa.validation;

import org.nuxeo.ecm.core.api.DocumentModel;

/**
 * 
 * @author mkalam-alami
 *
 */
public interface ValidationService {
    
    /**
     * Validates a workspace again the given environment 
     * @param workspace
     * @param environment
     */
    void validate(DocumentModel workspace, DocumentModel environment);

}
