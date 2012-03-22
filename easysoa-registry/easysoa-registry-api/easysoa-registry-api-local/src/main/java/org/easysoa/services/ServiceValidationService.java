package org.easysoa.services;

import java.util.List;
import java.util.SortedSet;

import org.easysoa.validation.CorrelationMatch;
import org.easysoa.validation.ServiceValidator;
import org.easysoa.validation.ValidationResultList;
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
     * @return A list of validation errors
     * @throws Exception
     */
	ValidationResultList validateServices(CoreSession session, DocumentModel model) throws Exception;

    /**
     * Finds correlated services from the reference environment of the given service.
     * Note: if the service explicitly references another one, this function will only return
     * that service, with a correlation rate of 1.0 (100%).
     * @param session
     * @param service
     * @return A list of correlation match, ordered in decreasing correlation rate.
     * @throws Exception 
     */
    SortedSet<CorrelationMatch> findCorrelatedServices(CoreSession session, DocumentModel service) throws Exception;
    
    /**
     * 
     * @return The actual validator list: it should be read only
     */
    List<ServiceValidator> getValidators();
    
}
