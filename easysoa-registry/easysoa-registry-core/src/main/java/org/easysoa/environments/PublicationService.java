package org.easysoa.environments;

import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;

/**
 * 
 * @author mkalam-alami
 *
 */
public interface PublicationService {
    
    /**
     * Publishes the given service in a certain environment 
     * @param session
     * @param model
     * @param environmentName
     */
    void publish(CoreSession session, DocumentModel model, String environmentName);

    /**
     * Removes all published versions of a document
     * @param session
     * @param doc
     */
    void unpublish(CoreSession session, DocumentModel model);

}
