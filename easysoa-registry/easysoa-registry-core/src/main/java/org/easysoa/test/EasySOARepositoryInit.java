package org.easysoa.test;

import org.easysoa.DomainInit;
import org.easysoa.doctypes.AppliImpl;
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
        doc.setProperty("dublincore", "title", DomainInit.DOMAIN_TITLE);
        doc = session.createDocument(doc);
        session.saveDocument(doc);

        doc = session.createDocumentModel("/default-domain/", "workspaces",
                "WorkspaceRoot");
        doc.setProperty("dublincore", "title", DomainInit.WORKSPACE_ROOT_TITLE);
        doc = session.createDocument(doc);
        session.saveDocument(doc);
        
        doc = session.createDocumentModel("/default-domain/workspaces", "(Default)",
                "Workspace");
        doc.setProperty("dublincore", "title", AppliImpl.DEFAULT_APPLIIMPL_TITLE);
        doc.setProperty(AppliImpl.SCHEMA, AppliImpl.PROP_URL, AppliImpl.DEFAULT_APPLIIMPL_URL);
        doc = session.createDocument(doc);
        session.saveDocument(doc);

    }

}
