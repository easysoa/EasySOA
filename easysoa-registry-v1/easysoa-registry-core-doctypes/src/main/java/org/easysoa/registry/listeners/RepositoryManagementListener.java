package org.easysoa.registry.listeners;

import org.apache.log4j.Logger;
import org.easysoa.registry.services.DocumentService;
import org.easysoa.registry.types.RepositoryDoctype;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentRef;
import org.nuxeo.ecm.core.api.PathRef;
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
                DocumentModel repositoryDocument = documentManager.move(sourceDocument.getRef(),
                        new PathRef(sourceFolderPath), sourceDocument.getName());
                documentManager.createProxy(repositoryDocument.getRef(), currentParentRef);
            }
        } catch (Exception e) {
            logger.error("Failed to check document after creation", e);
        }
        
        
    }
    

}
