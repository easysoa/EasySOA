package org.easysoa.registry;

import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentModelList;
import org.nuxeo.ecm.core.api.DocumentRef;
import org.nuxeo.ecm.core.api.model.PropertyException;

/**
 * 
 * @author mkalam-alami
 *
 */
public interface DocumentService {

    /**
     * Helper for general document creation.
     * Direct use is not recommended for SoaNode types.
     * 
     * @return
     * @throws ClientException
     */
    DocumentModel createDocument(CoreSession documentManager, String doctype, String name,
            String parentPath, String title) throws ClientException;
    
    /**
     * Creates a SoaNode document. If a document of the same identifier
     * exists, returns it instead. If the target path is not the expected path within the repository,
     * a document will be stored in the repository, and proxied at the wanted destination.
     * 
     * @throws ClientException
     */
    DocumentModel create(CoreSession documentManager, SoaNodeId identifier,
            String parentPath, String title) throws ClientException;

    /**
     * Creates a document and puts it in the repository. If a document of the same identifier
     * exists, returns it instead.
     * Works only with SoaNode types (returns null otherwise).
     * 
     * @throws ClientException
     */
    DocumentModel create(CoreSession documentManager, SoaNodeId identifier, String title)
            throws ClientException;
    
    /**
     * Copies a document at the target destination.
     * Recommended for SoaNodes as it handles proxies correctly.
     */
    DocumentModel copy(CoreSession documentManager, DocumentModel sourceModel, DocumentRef destRef)
            throws ClientException;

    /**
     * Deletes the specified SoaNode from the repository, including all proxies.
     * @return true if the document existed and was succesfully deleted
     */
    boolean delete(CoreSession documentManager, SoaNodeId soaNodeId) throws ClientException;

    
    /**
     * Deletes the specified SoaNode proxy from a specific location.
     * @return true if the document existed and was succesfully deleted
     */
    boolean deleteProxy(CoreSession documentManager, SoaNodeId soaNodeId, String parentPath) throws ClientException;
    
    DocumentModel findDocument(CoreSession documentManager, String type, String name)
    throws ClientException;

    /**
     * Finds any document given its type and name
     * If a SoaNode, returns the source (non-proxy) from the repository 
     * @param documentManager
     * @param identifier
     * @return The document, or null if it doesn't exist
     * @throws ClientException
     */
    DocumentModel find(CoreSession documentManager, SoaNodeId identifier)
            throws ClientException;

    /**
     * Find a proxy at a specific location
     */
    DocumentModel findProxy(CoreSession documentManager, SoaNodeId identifier, String parentPath)
            throws ClientException;
    
    /**
     * Find all proxies for a document given its type and name
     */
    DocumentModelList findProxies(CoreSession documentManager, SoaNodeId identifier)
            throws ClientException;
    
    /**
     * Find all proxies for a document given a model (either one proxy or the source)
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
    
    boolean hasChild(CoreSession documentManager, DocumentModel document, SoaNodeId childId)
            throws ClientException;

    String getSourceFolderPath(String doctype);

    String getSourcePath(SoaNodeId identifier);

    void ensureSourceFolderExists(CoreSession documentManager, String doctype)
            throws ClientException;

    SoaNodeId createSoaNodeId(DocumentModel model) throws PropertyException, ClientException;
    
}