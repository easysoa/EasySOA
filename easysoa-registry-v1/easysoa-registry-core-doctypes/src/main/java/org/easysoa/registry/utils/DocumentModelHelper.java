package org.easysoa.registry.utils;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentModelList;
import org.nuxeo.ecm.core.api.impl.DocumentModelListImpl;
import org.nuxeo.ecm.platform.types.TypeManager;
import org.nuxeo.runtime.api.Framework;

public class DocumentModelHelper {

    private static final Log log = LogFactory.getLog(DocumentModelHelper.class);
    
    public static String getDocumentTypeLabel(DocumentModel model) {
        try {
            TypeManager typeManager = Framework.getService(TypeManager.class);
            return typeManager.getType(model.getType()).getLabel();
        } catch (Exception e) {
            log.warn("Failed to fetch document type label, falling back to type name instead.");
            return model.getType();
        }
    }

    public static DocumentModelList getParents(CoreSession documentManager, DocumentModel documentModel) throws Exception {
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
    
    public static DocumentModel createDocument(CoreSession documentManager, String doctype, String parentPath, String name, String title) throws ClientException {
        DocumentModel documentModel = documentManager.createDocumentModel(doctype);
        documentModel.setPathInfo(parentPath, name);
        documentModel.setProperty("dublincore", "title", title);
        return documentManager.createDocument(documentModel);
    }

}
