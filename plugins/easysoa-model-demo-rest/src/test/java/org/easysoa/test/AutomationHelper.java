package org.easysoa.test;

import org.easysoa.doctypes.EasySOADoctype;
import org.nuxeo.ecm.automation.client.jaxrs.OperationRequest;
import org.nuxeo.ecm.automation.client.jaxrs.Session;
import org.nuxeo.ecm.automation.client.jaxrs.impl.HttpAutomationClient;
import org.nuxeo.ecm.automation.client.jaxrs.model.Documents;
import org.nuxeo.ecm.automation.core.operations.document.Query;

/**
 * Thin layer to ease the use of the Nuxeo automation API.
 * @author mkalam-alami
 *
 */
public class AutomationHelper {

	private HttpAutomationClient client;
	private Session session;
	
	public AutomationHelper(String nuxeoUrl) throws Exception {
        client = new HttpAutomationClient(nuxeoUrl+"/site/automation");
        session = client.getSession("Administrator", "Administrator");
	}

	public Documents findDocumentByUrl(String doctype, String url) throws Exception {
		return query("SELECT * FROM " + doctype + " WHERE " + 
			EasySOADoctype.getSchemaPrefix(doctype) + "url = '" + url + "'");
	}
	
	public Documents query(String query) throws Exception {
		OperationRequest request = session.newRequest(Query.ID);
		request.setHeader("X-NXDocumentProperties", "*");
		request.set("query", query);
		return (Documents) request.execute();
	}
	
}
