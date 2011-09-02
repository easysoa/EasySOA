package org.openwide.easysoa.test.mock.nuxeomock;

import javax.ws.rs.core.Request;
import javax.ws.rs.core.UriInfo;
import org.apache.log4j.Logger;
import org.openwide.easysoa.test.Utilities;

public class NuxeoMockImpl implements NuxeoMock {

	private final static String REGISTRY_REQUEST = "{\"params\":{\"query\":\"SELECT * FROM Document WHERE ecm:path STARTSWITH '/default-domain/workspaces/' AND ecm:currentLifeCycleState <> 'deleted' ORDER BY ecm:path\"}}";
	
	/**
	 * Logger
	 */
	@SuppressWarnings("unused")
    private static Logger logger = Logger.getLogger(NuxeoMockImpl.class.getName());
	
	@Override
	public String processNotificationRequest(String type) {
		// Returns only a OK response
		return "{\"result\": \"ok\"}";
	}

	@Override
	public String processAutomationRequest(UriInfo ui, Request request, String body) {
		System.out.println("Body request : " + body);
		// Registry content request
		if(REGISTRY_REQUEST.equals(body)) {
			return Utilities.readResponseFile("src/test/resources/nuxeoMockMessages/nuxeoRegistryContent.json");
		}
		// SOAP test request
		else if(body.contains("dc:title = 'meteo'")){
			return Utilities.readResponseFile("src/test/resources/nuxeoMockMessages/nuxeoResponseTestSoap.json");
		} 
		// REST test request
		else {
			return Utilities.readResponseFile("src/test/resources/nuxeoMockMessages/nuxeoResponseTestRest.json");
		}
	}

}
