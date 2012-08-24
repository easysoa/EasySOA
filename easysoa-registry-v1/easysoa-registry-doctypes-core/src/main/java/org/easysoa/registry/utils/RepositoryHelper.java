package org.easysoa.registry.utils;

import org.easysoa.registry.types.Repository;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.PathRef;

public class RepositoryHelper {

    public static final PathRef REPOSITORY_REF = new PathRef(Repository.REPOSITORY_PATH);
    
    public static DocumentModel getRepositoryInstance(CoreSession documentManager) throws ClientException {
        if (documentManager.exists(REPOSITORY_REF)) {
            return documentManager.getDocument(REPOSITORY_REF);
        }
        else {
            DocumentModel repositoryModel = documentManager.createDocumentModel("/default-domain", "repository", Repository.DOCTYPE);
            repositoryModel.setProperty("dublincore", "title", Repository.REPOSITORY_TITLE);
            repositoryModel = documentManager.createDocument(repositoryModel);
            documentManager.save();
            return repositoryModel;
        }
    }
    
    public static void ensureRepositoryInstanceExists(CoreSession documentManager) throws ClientException {
        getRepositoryInstance(documentManager);
    }
    
}
