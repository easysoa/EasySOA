package org.easysoa.services;

import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;

/**
 * 
 * @author mkalam-alami
 *
 */
public interface PublicationService {
    
    /**
     * Publishes the given document in a certain environment (and recursively all childs) 
     * @param session
     * @param model
     * @param environmentName
     */
    void publish(CoreSession session, DocumentModel model, String environmentName);

    /**
     * Removes the published version of a document in a certain environment
     * @param session
     * @param doc
     */
    void unpublish(CoreSession session, DocumentModel model, String environmentName);
    
    /**
     * Removes all published versions of a document
     * @param session
     * @param doc
     */
    void unpublish(CoreSession session, DocumentModel model);
    
    DocumentModel forkEnvironment(CoreSession session, DocumentModel sectionModel, String newWorkspaceName) throws Exception;

	void updateFromReferenceEnvironment(CoreSession session, DocumentModel appliImplModel) throws Exception;
    
}
