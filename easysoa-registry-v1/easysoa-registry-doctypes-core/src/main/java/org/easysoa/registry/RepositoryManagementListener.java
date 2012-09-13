package org.easysoa.registry;

import java.io.Serializable;

import org.apache.log4j.Logger;
import org.easysoa.registry.systems.IntelligentSystemTreeService;
import org.easysoa.registry.types.SoaNode;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentRef;
import org.nuxeo.ecm.core.api.PathRef;
import org.nuxeo.ecm.core.api.event.DocumentEventTypes;
import org.nuxeo.ecm.core.event.Event;
import org.nuxeo.ecm.core.event.EventContext;
import org.nuxeo.ecm.core.event.EventListener;
import org.nuxeo.ecm.core.event.impl.DocumentEventContext;
import org.nuxeo.runtime.api.Framework;

/**
 * 
 * @author mkalam-alami
 *
 */
public class RepositoryManagementListener implements EventListener {

    private static Logger logger = Logger.getLogger(RepositoryManagementListener.class);
    
    @Override
    public void handleEvent(Event event) throws ClientException {

        // Ensure event nature
        EventContext context = event.getContext();
        if (!(context instanceof DocumentEventContext)) {
            return;
        }
        DocumentEventContext documentContext = (DocumentEventContext) context;
        DocumentModel sourceDocument = documentContext.getSourceDocument();
        if (!sourceDocument.hasFacet("SoaNode")) {
            return;
        }
        
        try {
            // Initialize
            CoreSession documentManager = documentContext.getCoreSession();
            DocumentService documentService = Framework.getService(DocumentService.class);
            
            // If a document has been created through the Nuxeo UI, move it to the repository and leave only a proxy
            DocumentRef currentParentRef = sourceDocument.getParentRef();
            String sourceFolderPath = documentService.getSourceFolderPath(sourceDocument.getType());
            if (!sourceDocument.isProxy() && !sourceDocument.getPathAsString().startsWith(sourceFolderPath)) {
                documentService.ensureSourceFolderExists(documentManager, sourceDocument.getType());
                String soaName = (String) sourceDocument.getPropertyValue(SoaNode.XPATH_SOANAME);
                if (soaName == null || soaName.isEmpty()) {
                    sourceDocument.setPropertyValue(SoaNode.XPATH_SOANAME, sourceDocument.getName());
                }
                
                PathRef sourcePathRef = new PathRef(documentService.getSourcePath(
                        documentService.createSoaNodeId(sourceDocument)));
                DocumentModel repositoryDocument;
                if (documentManager.exists(sourcePathRef)) {
                    // If the source document already exists, only keep one
                    repositoryDocument = documentManager.getDocument(sourcePathRef);
                    repositoryDocument.copyContent(sourceDocument); // Merge
                    documentManager.saveDocument(repositoryDocument);
                    documentManager.removeDocument(sourceDocument.getRef());
                }
                else {
                    // Move to repository otherwise
                    repositoryDocument = documentManager.move(sourceDocument.getRef(),
                        new PathRef(sourceFolderPath),
                        sourceDocument.getName());
                }
                
                // Create a proxy at the expected location
                documentManager.createProxy(repositoryDocument.getRef(), currentParentRef);
            }
            documentManager.save();
            
            // Intelligent system trees update
            IntelligentSystemTreeService intelligentSystemTreeServiceCache =
                    Framework.getService(IntelligentSystemTreeService.class);
            intelligentSystemTreeServiceCache.handleDocumentModel(documentManager, sourceDocument,
                    !DocumentEventTypes.DOCUMENT_CREATED.equals(event.getName()));
            documentManager.save();
            
        } catch (Exception e) {
            logger.error("Failed to check document after creation", e);
        }
        
    }

}
