package org.easysoa.registry.utils;

import org.easysoa.registry.types.Repository;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;

public class RepositoryHelper {

    public static DocumentModel getRepositoryInstance(CoreSession documentManager) throws ClientException {
        if (documentManager.exists(Repository.REPOSITORY_REF)) {
            return documentManager.getDocument(Repository.REPOSITORY_REF);
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
