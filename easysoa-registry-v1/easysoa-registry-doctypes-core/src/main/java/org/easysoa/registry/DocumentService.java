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

    /**
     * Creates a document.
     * 
     * @return
     * @throws ClientException
     */
    DocumentModel create(CoreSession documentManager, SoaNodeId identifier,
            String parentPath, String title) throws ClientException;

    /**
     * Creates a document and puts it in the repository.
     * Works only with SoaNode types (returns null otherwise) 
     * 
     * @return
     * @throws ClientException
     */
    DocumentModel create(CoreSession documentManager, SoaNodeId identifier, String title)
            throws ClientException;
    
    /**
     * Copies a document at the target destination.
     * Recommanded for SoaNodes as it handles proxies correctly.
     */
    DocumentModel copy(CoreSession documentManager, DocumentModel sourceModel, DocumentRef destRef)
            throws ClientException;
    
    /**
     * Finds any document given its type and name
     * If a SoaNode, returns the source (non-proxy) from the repository
     */
    DocumentModel find(CoreSession documentManager, SoaNodeId identifier)
            throws ClientException;

    /**
     * Find all proxies for a document given its type and name
     */
    DocumentModelList findProxies(CoreSession documentManager, DocumentModel model)
            throws ClientException;

    /**
     * Find all proxies and the repository source, given a type and name
     */
    DocumentModelList findAllInstances(CoreSession documentManager, SoaNodeId identifier)
            throws ClientException;

    /**
     * Find all proxies and the repository source, given a model (either one proxy or the source)
     */
    DocumentModelList findAllInstances(CoreSession documentManager, DocumentModel model)
            throws ClientException;

    /**
     * Find all documents that contain either the specified model, or any other instance of it.
     */
    DocumentModelList findAllParents(CoreSession documentManager, DocumentModel documentModel)
            throws Exception;

    String getSourceFolderPath(String doctype);

    String getSourcePath(SoaNodeId identifier);

    void ensureSourceFolderExists(CoreSession documentManager, String doctype)
            throws ClientException;

    
}