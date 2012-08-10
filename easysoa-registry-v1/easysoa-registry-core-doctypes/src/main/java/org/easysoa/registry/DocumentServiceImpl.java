package org.easysoa.registry;

import org.easysoa.registry.types.IntelligentSystemDoctype;
import org.easysoa.registry.types.RepositoryDoctype;
import org.easysoa.registry.utils.DocumentModelHelper;
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

    public DocumentModel create(CoreSession documentManager, String doctype, String parentPath, String name, String title) throws ClientException {
        // Basic behavior
        if (!documentManager.getDocumentType(doctype).getFacets().contains("SoaNode")) {
            
            DocumentModel documentModel = documentManager.createDocumentModel(parentPath, name, doctype);
            documentModel.setProperty("dublincore", "title", title);
            return documentManager.createDocument(documentModel);
        }
        // SoaNodes must be stored in the repository, and only have live proxies in the system trees
        else {
            boolean createProxy = false;
            if (!parentPath.equals(RepositoryDoctype.REPOSITORY_PATH)) {
                createProxy = true;
            }
            
            // Create or fetch source document
            ensureSourceFolderExists(documentManager, doctype);
            PathRef sourceRef = new PathRef(getSourcePath(doctype, name));
            DocumentModel documentModel;
            if (!documentManager.exists(sourceRef)) {
                documentModel = documentManager.createDocumentModel(doctype);
                documentModel.setPathInfo(getSourceFolderPath(doctype), name);
                documentModel.setProperty("dublincore", "title", title);
                documentModel = documentManager.createDocument(documentModel);
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

    public DocumentModel find(CoreSession documentManager, String doctype, String name) throws ClientException {
        String query = NXQLQueryBuilder.getQuery("SELECT * FROM ? WHERE " + NXQL.ECM_NAME + " = '?' AND " + NXQL.ECM_ISPROXY + " = 0",
                new Object[] { doctype, name },
                false, true);
        DocumentModelList results = documentManager.query(query);
        return results.size() > 0 ? results.get(0) : null;
    }

    public DocumentModelList findProxies(CoreSession documentManager, DocumentModel model) throws ClientException {
        return documentManager.getProxies(model.getRef(), null);
    }
   
    public DocumentModelList findAllInstances(CoreSession documentManager, String doctype, String name) throws ClientException {
        String query = NXQLQueryBuilder.getQuery("SELECT * FROM ? WHERE " + NXQL.ECM_NAME + " = '?'",
                new Object[] { doctype, name },
                false, true);
        return documentManager.query(query);
    }
    
    public DocumentModelList findAllInstances(CoreSession documentManager, DocumentModel model) throws ClientException {
        return findAllInstances(documentManager, model.getType(), model.getName());
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
        return RepositoryDoctype.REPOSITORY_PATH + '/' + doctype; 
    }
    
    public String getSourcePath(String doctype, String name) {
        return getSourceFolderPath(doctype) + '/' + name;
    }
    
    public void ensureSourceFolderExists(CoreSession documentManager, String doctype) throws ClientException {
        RepositoryDoctype.getRepositoryInstance(documentManager);
        getSourceFolder(documentManager, doctype);
    }

    private DocumentModel getSourceFolder(CoreSession documentManager, String doctype) throws ClientException {
        PathRef sourceFolderRef = new PathRef(getSourceFolderPath(doctype));
        if (documentManager.exists(sourceFolderRef)) {
            return documentManager.getDocument(sourceFolderRef);
        }
        else {
            DocumentModel sourceFolderModel = documentManager.createDocumentModel(RepositoryDoctype.REPOSITORY_PATH,
                    doctype, IntelligentSystemDoctype.DOCTYPE);
            sourceFolderModel.setProperty("dublincore", "title", DocumentModelHelper.getDocumentTypeLabel(doctype) + "s");
            sourceFolderModel = documentManager.createDocument(sourceFolderModel);
            return sourceFolderModel;
        }
    }


}
