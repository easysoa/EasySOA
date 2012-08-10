package org.easysoa.registry.systems;

import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;

public interface IntelligentSystemTreeService {

    void handleDocumentModel(CoreSession documentManager, DocumentModel model) throws Exception;

}
