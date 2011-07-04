package org.easysoa.test;

import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.test.annotations.RepositoryInit;

/**
 * Default repository initializer that create the default DM doc hierarchy.
 */
public class EasySOARepositoryInit implements RepositoryInit {

    public void populate(CoreSession session) throws ClientException {
    	
        DocumentModel doc = session.createDocumentModel("/", "default-domain",
                "Domain");
        doc.setProperty("dublincore", "title", "Default domain");
        doc = session.createDocument(doc);
        session.saveDocument(doc);

        doc = session.createDocumentModel("/default-domain/", "workspaces",
                "WorkspaceRoot");
        doc.setProperty("dublincore", "title", "Workspaces");
        doc = session.createDocument(doc);
        session.saveDocument(doc);
        
        doc = session.createDocumentModel("/default-domain/workspaces", "(Default)",
                "Workspace");
        doc.setProperty("dublincore", "title", "Default application");
        doc = session.createDocument(doc);
        session.saveDocument(doc);

    }

}
