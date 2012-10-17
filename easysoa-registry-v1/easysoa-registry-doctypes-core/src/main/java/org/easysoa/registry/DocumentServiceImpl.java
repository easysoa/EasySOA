package org.easysoa.registry;

import java.util.ArrayList;
import java.util.List;

import org.easysoa.registry.types.IntelligentSystem;
import org.easysoa.registry.types.Repository;
import org.easysoa.registry.types.SoaNode;
import org.easysoa.registry.utils.DocumentModelHelper;
import org.easysoa.registry.utils.RepositoryHelper;
import org.nuxeo.common.utils.Path;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentModelList;
import org.nuxeo.ecm.core.api.DocumentRef;
import org.nuxeo.ecm.core.api.PathRef;
import org.nuxeo.ecm.core.api.impl.DocumentModelListImpl;
import org.nuxeo.ecm.core.api.model.PropertyException;
import org.nuxeo.ecm.core.api.model.PropertyNotFoundException;
import org.nuxeo.ecm.core.query.sql.NXQL;
import org.nuxeo.ecm.platform.query.nxql.NXQLQueryBuilder;

public class DocumentServiceImpl implements DocumentService {

    public DocumentModel createDocument(CoreSession documentManager, String doctype, String name, String parentPath, String title) throws ClientException {
        // TODO if doctype belongs to SoaNode subtypes, throw new Exception("createDocument() doesn't work for SoaNode types, rather use create()")
        DocumentModel documentModel = documentManager.createDocumentModel(doctype);
        documentModel.setPathInfo(parentPath, name);
        documentModel.setProperty("dublincore", "title", title);
        documentModel = documentManager.createDocument(documentModel);
        return documentModel;
    }
    
    public DocumentModel create(CoreSession documentManager, SoaNodeId identifier, String parentPath) throws ClientException {
        String doctype = identifier.getType(), name = identifier.getName();
 
        if (isSoaNode(documentManager, doctype)) {
            boolean createProxy = false;
            if (!parentPath.equals(getSourceFolderPath(doctype))) {
                createProxy = true;
            }
            
            // Create or fetch source document
            ensureSourceFolderExists(documentManager, doctype);
            PathRef sourceRef = new PathRef(getSourcePath(identifier));
            DocumentModel documentModel;
            if (!documentManager.exists(sourceRef)) {
                documentModel = createDocument(documentManager, doctype, name, getSourceFolderPath(doctype), name);
                documentModel.setPropertyValue(SoaNode.XPATH_SOANAME, name);
                documentManager.saveDocument(documentModel);
            }
            else {
                documentModel = documentManager.getDocument(sourceRef);
            }
            
            // Create proxy if needed (but make sure the parent is the instance of the repository,
            // otherwise the child proxy will only be visible in the context of the parent proxy)
            if (createProxy) {
                PathRef parentRef = new PathRef(parentPath);
                DocumentModel parentModel = documentManager.getDocument(parentRef);
                if (parentModel != null) {
                    if (parentModel.isProxy()) {
                        parentModel = find(documentManager, createSoaNodeId(parentModel));
                    }
                }
                else {
                    throw new ClientException("Invalid parent path: " + parentPath);
                }
                return documentManager.createProxy(documentModel.getRef(), parentModel.getRef());
            }
            else {
                return documentModel;
            }
        }
        else {
            return null;
        }
    }

    public DocumentModel create(CoreSession documentManager, SoaNodeId identifier) throws ClientException {
        String doctype = identifier.getType(), name = identifier.getName();
        
        if (isSoaNode(documentManager, doctype)) {
            ensureSourceFolderExists(documentManager, doctype);
            PathRef sourceRef = new PathRef(getSourcePath(identifier));
            DocumentModel documentModel;
            if (!documentManager.exists(sourceRef)) {
                documentModel = documentManager.createDocumentModel(doctype);
                documentModel.setPathInfo(getSourceFolderPath(doctype), name);
                documentModel.setPropertyValue("dc:title", name);
                documentModel.setPropertyValue(SoaNode.XPATH_SOANAME, name);
                return documentManager.createDocument(documentModel);
            }
            else {
                return documentManager.getDocument(sourceRef);
            }
        }
        else {
            return null;
        }
    }
    @Override
    public DocumentModel copy(CoreSession documentManager, DocumentModel sourceModel, DocumentRef ref) throws ClientException {
        if (sourceModel.isProxy()) {
            return documentManager.copy(sourceModel.getRef(), ref, sourceModel.getName());
        }
        else {
            return documentManager.createProxy(sourceModel.getRef(), ref);
        }
    }

    @Override
    public boolean delete(CoreSession documentManager, SoaNodeId soaNodeId) throws ClientException {
        DocumentModel sourceDocument = find(documentManager, soaNodeId);
        if (sourceDocument != null) {
            documentManager.removeDocument(sourceDocument.getRef());
            return true;
        }
        else {
            return false;
        }
    }

    @Override
    public boolean deleteProxy(CoreSession documentManager, SoaNodeId soaNodeId, String parentPath)
            throws ClientException {
        DocumentModelList instances = findAllInstances(documentManager, soaNodeId);
        for (DocumentModel instance : instances) {
            if (instance.getPath().removeLastSegments(1).equals(new Path(parentPath))) {
                documentManager.removeDocument(instance.getRef());
                return true;
            }
        }   
        return false;
    }

    public DocumentModel findDocument(CoreSession documentManager, String type, String name) throws ClientException {
        String query = NXQLQueryBuilder.getQuery("SELECT * FROM ? WHERE " + NXQL.ECM_NAME + " = '?'"
                + PROXIES_QUERY_FILTER + DELETED_DOCUMENTS_QUERY_FILTER + VERSIONS_QUERY_FILTER,
                new Object[] { type, name },
                false, true);
        DocumentModelList results = documentManager.query(query);
        return results.size() > 0 ? results.get(0) : null;
    }
    
    public DocumentModel find(CoreSession documentManager, SoaNodeId identifier) throws ClientException {
        String query = NXQLQueryBuilder.getQuery("SELECT * FROM ? WHERE " + SoaNode.XPATH_SOANAME + " = '?'"
                + PROXIES_QUERY_FILTER + DELETED_DOCUMENTS_QUERY_FILTER + VERSIONS_QUERY_FILTER,
                new Object[] { identifier.getType(), identifier.getName() },
                false, true);
        DocumentModelList results = documentManager.query(query);
        return results.size() > 0 ? results.get(0) : null;
    }

    @Override
    public DocumentModelList findProxies(CoreSession documentManager, SoaNodeId identifier)
            throws ClientException {
        String query = NXQLQueryBuilder.getQuery("SELECT * FROM ? WHERE " + SoaNode.XPATH_SOANAME + " = '?'"
                + NON_PROXIES_QUERY_FILTER + DELETED_DOCUMENTS_QUERY_FILTER + VERSIONS_QUERY_FILTER,
                new Object[] { identifier.getType(), identifier.getName() },
                false, true);
        return documentManager.query(query);
    }

    public DocumentModelList findProxies(CoreSession documentManager, DocumentModel model) throws ClientException {
        if (!model.isProxy()) {
            return documentManager.getProxies(model.getRef(), null);
        }
        else {
            return findProxies(documentManager, createSoaNodeId(model));
        }
    }
   
    @Override
    public DocumentModel findProxy(CoreSession documentManager, SoaNodeId identifier,
            String parentPath) throws ClientException {
        // Check parent
        PathRef parentRef = new PathRef(parentPath);
        DocumentModel parentModel = documentManager.getDocument(parentRef);
        if (parentModel.isProxy()) {
            parentModel = find(documentManager, createSoaNodeId(parentModel));
        }
        
        // Find proxy among children
        DocumentModelList childrenModels = documentManager.getChildren(parentModel.getRef());
        for (DocumentModel childModel : childrenModels) {
            if (createSoaNodeId(childModel).equals(identifier)) {
                return childModel;
            }
        }
        return null;
    }

    public DocumentModelList findAllInstances(CoreSession documentManager, SoaNodeId identifier) throws ClientException {
        String query = NXQLQueryBuilder.getQuery("SELECT * FROM ? WHERE " + SoaNode.XPATH_SOANAME + " = '?'"
                + DELETED_DOCUMENTS_QUERY_FILTER + VERSIONS_QUERY_FILTER,
                new Object[] { identifier.getType(), identifier.getName() },
                false, true);
        return documentManager.query(query);
    }
    
    public DocumentModelList findAllInstances(CoreSession documentManager, DocumentModel model) throws ClientException {
        return findAllInstances(documentManager, createSoaNodeId(model));
    }
    
    public DocumentModelList findAllParents(CoreSession documentManager, DocumentModel documentModel) throws Exception {
        // Find working copy of document
        DocumentModel workingModel;
        if (documentModel.isProxy()) {
            workingModel = documentManager.getWorkingCopy(documentModel.getRef());
        }
        else {
            workingModel = documentModel;
        }
    
        // Build proxies list
        DocumentModelList modelInstances = documentManager.getProxies(documentModel.getRef(), null);
        modelInstances.add(workingModel);
        
        // Fetch parents
        DocumentModelList parents = new DocumentModelListImpl();
        for (DocumentModel modelInstance : modelInstances) {
            DocumentModel parentDocument = documentManager.getParentDocument(modelInstance.getRef());
            if (!parents.contains(parentDocument)) {
                parents.add(parentDocument);
            }
        }
        return parents;
    }

    @Override
    public boolean hasChild(CoreSession documentManager, DocumentModel document,
            SoaNodeId childId) throws ClientException {
        if (document != null && childId != null) {
            DocumentModelList children = documentManager.getChildren(document.getRef(), childId.getType());
            for (DocumentModel child : children) {
                if (createSoaNodeId(child).equals(childId)) {
                    return true;
                }
            }
        }
        return false;
    }

    public String getSourceFolderPath(String doctype) {
        return Repository.REPOSITORY_PATH + '/' + doctype; 
    }
    
    public String getSourcePath(SoaNodeId identifier) {
        return getSourceFolderPath(identifier.getType()) + '/' + identifier.getName();
    }
    
    public void ensureSourceFolderExists(CoreSession documentManager, String doctype) throws ClientException {
        RepositoryHelper.getRepositoryInstance(documentManager);
        getSourceFolder(documentManager, doctype);
    }
    
    public DocumentModelList getChildren(CoreSession session, DocumentRef parentRef, String type) throws ClientException {
        parentRef = session.getWorkingCopy(parentRef).getRef(); // making sure it's not a proxy
        ///DocumentModelList serviceProxyList = session.getProxies(parentRef, null);
        return session.getChildren(parentRef, type);
    }

    private DocumentModel getSourceFolder(CoreSession documentManager, String doctype) throws ClientException {
        PathRef sourceFolderRef = new PathRef(getSourceFolderPath(doctype));
        if (documentManager.exists(sourceFolderRef)) {
            return documentManager.getDocument(sourceFolderRef);
        }
        else {
            DocumentModel sourceFolderModel = documentManager.createDocumentModel(Repository.REPOSITORY_PATH,
                    doctype, IntelligentSystem.DOCTYPE);
            sourceFolderModel.setProperty("dublincore", "title", DocumentModelHelper.getDocumentTypeLabel(doctype) + "s");
            sourceFolderModel = documentManager.createDocument(sourceFolderModel);
            return sourceFolderModel;
        }
    }
    
    public SoaNodeId createSoaNodeId(DocumentModel model) throws ClientException {
        try {
            return new SoaNodeId(model.getType(), (String) model.getPropertyValue(SoaNode.XPATH_SOANAME));
        }
        catch (PropertyNotFoundException e) {
            throw new ClientException("Invalid document type (" + model.getType() + "), an SoaNode is expected");
        }
    }

    @Override
    public List<SoaNodeId> createSoaNodeIds(DocumentModel... models) throws PropertyException, ClientException {
        List<SoaNodeId> soaNodeIds = new ArrayList<SoaNodeId>();
        for (DocumentModel model : models) {
            soaNodeIds.add(createSoaNodeId(model));
        }
        return soaNodeIds;
    }
    
    public boolean isSoaNode(CoreSession documentManager, String doctype) throws ClientException {
        return documentManager.getDocumentType(doctype).getFacets().contains("SoaNode");
    }


}
