package org.easysoa.validation;

import java.util.List;

import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;

/**
 * 
 * @author mkalam-alami
 *
 */
public interface ServiceValidationService {
    
    /**
     * Validates all of the services contained in the given document
     * (or the one service given in parameter), against the workspace's reference environment. 
     * @param session
     * @param model
     * @throws Exception
     */
    void validateServices(CoreSession session, DocumentModel model) throws Exception;

    /**
     * 
     * @return The actual validator list: it should be read only
     */
    List<ServiceValidator> getValidators();

}
