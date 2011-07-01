package org.easysoa;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.easysoa.services.DocumentService;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.UnrestrictedSessionRunner;
import org.nuxeo.runtime.api.Framework;

/**
 * Creates all needed folders on Nuxeo startup.
 * 
 * @author mkalam-alami
 * 
 */
public class DomainInit extends UnrestrictedSessionRunner {
	
	@SuppressWarnings("unused")
	private static final Log log = LogFactory.getLog(DomainInit.class);
	

	public DomainInit(String repositoryName) {
		super(repositoryName);
	}

	/**
	 * Sets up default domain
	 */
	public void run() throws ClientException {
		
		DocumentModel root = session.getChildren(this.session.getRootDocument().getRef()).get(0);
		
		if (!root.getTitle().equals(DocumentService.DOMAIN_TITLE)) {
			root.setProperty("dublincore", "title", DocumentService.DOMAIN_TITLE);
			session.saveDocument(root);
		}
			
		for (DocumentModel rootChild : session.getChildren(root.getRef())) {
			if (!rootChild.getType().equals("WorkspaceRoot")) {
				session.removeDocument(rootChild.getRef());
			}
			else {
				rootChild.setProperty("dublincore", "title", DocumentService.WORKSPACE_ROOT_TITLE);
				session.saveDocument(rootChild);
				session.save();
			}
		}
		
		DocumentService docService;
		try {
			docService = Framework.getService(DocumentService.class);
			docService.getDefaultAppliImpl(session); // Touch Default Application
		} catch (Exception e) {
			// Failed to touch Default Application
		}
		
		session.save();
		
	}
	
}