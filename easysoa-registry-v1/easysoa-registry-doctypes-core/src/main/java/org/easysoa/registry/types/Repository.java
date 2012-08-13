package org.easysoa.registry.types;

import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.PathRef;

/**
 * 
 * @author mkalam-alami
 *
 */
public class Repository {
    
    public static final String DOCTYPE = "Repository";

    public static final String REPOSITORY_PATH = "/default-domain/repository";  
 
    public static final PathRef REPOSITORY_REF = new PathRef(REPOSITORY_PATH);
    
    private static final String REPOSITORY_TITLE = "Repository"; // TODO l10n

    public static DocumentModel getRepositoryInstance(CoreSession documentManager) throws ClientException {
        if (documentManager.exists(REPOSITORY_REF)) {
            return documentManager.getDocument(REPOSITORY_REF);
        }
        else {
            DocumentModel repositoryModel = documentManager.createDocumentModel("/default-domain", "repository", DOCTYPE);
            repositoryModel.setProperty("dublincore", "title", REPOSITORY_TITLE);
            repositoryModel = documentManager.createDocument(repositoryModel);
            documentManager.save();
            return repositoryModel;
        }
    }
    
    public static void ensureRepositoryInstanceExists(CoreSession documentManager) throws ClientException {
        getRepositoryInstance(documentManager);
    }
}
