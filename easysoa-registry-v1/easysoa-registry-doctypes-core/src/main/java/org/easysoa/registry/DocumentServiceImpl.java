package org.easysoa.registry;

import org.easysoa.registry.types.IntelligentSystem;
import org.easysoa.registry.types.Repository;
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
import org.nuxeo.ecm.core.query.sql.NXQL;
import org.nuxeo.ecm.platform.query.nxql.NXQLQueryBuilder;

public class DocumentServiceImpl implements DocumentService {

    public DocumentModel createDocument(CoreSession documentManager, String doctype, String name, String parentPath, String title) throws ClientException {
        DocumentModel documentModel = documentManager.createDocumentModel(doctype);
        documentModel.setPathInfo(parentPath, name);
        documentModel.setProperty("dublincore", "title", title);
        documentModel = documentManager.createDocument(documentModel);
        return documentModel;
    }
    
    public DocumentModel create(CoreSession documentManager, SoaNodeId identifier, String parentPath, String title) throws ClientException {
        String doctype = identifier.getType(), name = identifier.getName();
 
        if (documentManager.getDocumentType(doctype).getFacets().contains("SoaNode")) {
            boolean createProxy = false;
            if (!parentPath.equals(Repository.REPOSITORY_PATH)) {
                createProxy = true;
            }
            
            // Create or fetch source document
            ensureSourceFolderExists(documentManager, doctype);
            PathRef sourceRef = new PathRef(getSourcePath(identifier));
            DocumentModel documentModel;
            if (!documentManager.exists(sourceRef)) {
                documentModel = createDocument(documentManager, doctype, name, getSourceFolderPath(doctype), title);
            }
            else {
                documentModel = documentManager.getDocument(sourceRef);
            }
            
            // Create proxy if needed
            if (createProxy) {
                return documentManager.createProxy(documentModel.getRef(), new PathRef(parentPath));
            }
            else {
                return documentModel;
            }
        }
        else {
            return null;
        }
    }

    public DocumentModel create(CoreSession documentManager, SoaNodeId identifier, String title) throws ClientException {
        String doctype = identifier.getType(), name = identifier.getName();
        
        if (documentManager.getDocumentType(doctype).getFacets().contains("SoaNode")) {
            ensureSourceFolderExists(documentManager, doctype);
            PathRef sourceRef = new PathRef(getSourcePath(identifier));
            DocumentModel documentModel;
            if (!documentManager.exists(sourceRef)) {
                documentModel = documentManager.createDocumentModel(doctype);
                documentModel.setPathInfo(getSourceFolderPath(doctype), name);
                documentModel.setProperty("dublincore", "title", title);
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
        String query = NXQLQueryBuilder.getQuery("SELECT * FROM ? WHERE " + NXQL.ECM_NAME + " = '?' AND " + NXQL.ECM_ISPROXY + " = 0",
                new Object[] { type, name },
                false, true);
        DocumentModelList results = documentManager.query(query);
        return results.size() > 0 ? results.get(0) : null;
    }
    
    public DocumentModel find(CoreSession documentManager, SoaNodeId identifier) throws ClientException {
        return findDocument(documentManager, identifier.getType(), identifier.getName());
    }

    @Override
    public DocumentModelList findProxies(CoreSession documentManager, SoaNodeId identifier)
            throws ClientException {
        String query = NXQLQueryBuilder.getQuery("SELECT * FROM ? WHERE " + NXQL.ECM_NAME + " = '?' " +
        		"AND ecm:isProxy = 1",
                new Object[] { identifier.getType(), identifier.getName() },
                false, true);
        return documentManager.query(query);
    }

    public DocumentModelList findProxies(CoreSession documentManager, DocumentModel model) throws ClientException {
        if (!model.isProxy()) {
            return documentManager.getProxies(model.getRef(), null);
        }
        else {
            return findProxies(documentManager, SoaNodeId.fromModel(model));
        }
    }
   
    public DocumentModelList findAllInstances(CoreSession documentManager, SoaNodeId identifier) throws ClientException {
        String query = NXQLQueryBuilder.getQuery("SELECT * FROM ? WHERE " + NXQL.ECM_NAME + " = '?'",
                new Object[] { identifier.getType(), identifier.getName() },
                false, true);
        return documentManager.query(query);
    }
    
    public DocumentModelList findAllInstances(CoreSession documentManager, DocumentModel model) throws ClientException {
        return findAllInstances(documentManager, SoaNodeId.fromModel(model));
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
            parents.add(documentManager.getParentDocument(modelInstance.getRef()));
        }
        return parents;
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


}
