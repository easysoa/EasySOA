package org.easysoa.environments;

import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;

/**
 * 
 * @author mkalam-alami
 *
 */
public interface PublicationService {
    
    void publish(CoreSession session, DocumentModel model, String environmentName);

}
