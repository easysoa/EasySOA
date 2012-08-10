package org.easysoa.registry;

import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentModelList;
import org.nuxeo.ecm.core.api.DocumentRef;

/**
 * 
 * @author mkalam-alami
 *
 */
public interface DocumentService {

    DocumentModel create(CoreSession documentManager, String doctype, String parentPath,
            String name, String title) throws ClientException;

    DocumentModel copy(CoreSession documentManager, DocumentModel sourceModel, DocumentRef destRef)
            throws ClientException;
    
    /**
     * If a SoaNode, returns the source (non-proxy) from the repository
     */
    DocumentModel find(CoreSession documentManager, String doctype, String name)
            throws ClientException;

    DocumentModelList findProxies(CoreSession documentManager, DocumentModel model)
            throws ClientException;

    DocumentModelList findAllInstances(CoreSession documentManager, String doctype, String name)
            throws ClientException;

    DocumentModelList findAllInstances(CoreSession documentManager, DocumentModel model)
            throws ClientException;

    DocumentModelList findAllParents(CoreSession documentManager, DocumentModel documentModel)
            throws Exception;

    String getSourceFolderPath(String doctype);

    String getSourcePath(String doctype, String name);

    void ensureSourceFolderExists(CoreSession documentManager, String doctype)
            throws ClientException;
    
}