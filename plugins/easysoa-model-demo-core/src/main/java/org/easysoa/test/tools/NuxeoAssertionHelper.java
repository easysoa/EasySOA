package org.easysoa.test.tools;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

import org.easysoa.doctypes.AppliImpl;
import org.easysoa.services.DocumentService;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.runtime.api.Framework;

/**
 * Allows to make assertions without having to know if we use
 * a test Nuxeo or an existing one.
 * @author mkalam-alami
 *
 */
public class NuxeoAssertionHelper { // TODO: When tested and stable, move the framework to specific project

	// Used in the case of an existing Nuxeo
	private AutomationHelper automation = null;
	
	// Used when testing with test Nuxeo
	private CoreSession coreSession = null;
	
	public NuxeoAssertionHelper(CoreSession session) throws Exception {
		coreSession = session;
	}
			
	public NuxeoAssertionHelper(String nuxeoUrl) throws Exception {
		automation = new AutomationHelper(nuxeoUrl);
	}	

	public void assertDocumentExists(String doctype, String url) throws Exception {
		if (coreSession != null) {
			DocumentService docService = Framework.getService(DocumentService.class);
			DocumentModel model = null;
			// TODO DocumentService refactoring
			if (AppliImpl.DOCTYPE.equals(doctype))
				model = docService.findAppliImpl(coreSession, url);
			else if (AppliImpl.DOCTYPE.equals(doctype))
				model = docService.findServiceApi(coreSession, url);
			else if (AppliImpl.DOCTYPE.equals(doctype))
				model = docService.findService(coreSession, url);
			else if (AppliImpl.DOCTYPE.equals(doctype))
				model = docService.findServiceReference(coreSession, url);
			assertNotNull(model);
		}
		else {
			assertFalse(automation.findDocumentByUrl(doctype, url).isEmpty());
		}
	}
	
}
