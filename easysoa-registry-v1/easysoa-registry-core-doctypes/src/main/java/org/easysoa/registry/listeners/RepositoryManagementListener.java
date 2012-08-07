package org.easysoa.registry.listeners;

import org.easysoa.registry.types.RepositoryDoctype;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentRef;
import org.nuxeo.ecm.core.event.Event;
import org.nuxeo.ecm.core.event.EventContext;
import org.nuxeo.ecm.core.event.EventListener;
import org.nuxeo.ecm.core.event.impl.DocumentEventContext;

/**
 * 
 * @author mkalam-alami
 *
 */
public class RepositoryManagementListener implements EventListener {
    
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
        
        // Initialize
        CoreSession documentManager = documentContext.getCoreSession();
        DocumentModel repositoryRoot = RepositoryDoctype.getRepositoryInstance(documentManager);
        
        // Make sure the real document is always in the repository
        // The rest should only contain live proxies
        DocumentRef currentParentRef = sourceDocument.getParentRef();
        if (!sourceDocument.isProxy() && !repositoryRoot.getRef().equals(currentParentRef)) {
            // Move to the repository, or fetch existing repository document
            DocumentModel repositoryDocument = null;
            try {
                repositoryDocument = documentManager.getChild(repositoryRoot.getRef(), sourceDocument.getName());
            }
            catch (ClientException e) { // If the child doesn't exist
                repositoryDocument = documentManager.move(sourceDocument.getRef(), repositoryRoot.getRef(), sourceDocument.getName());
            }
            // Create proxy
            documentManager.createProxy(repositoryDocument.getRef(), currentParentRef);
        }
        
    }
    

}
