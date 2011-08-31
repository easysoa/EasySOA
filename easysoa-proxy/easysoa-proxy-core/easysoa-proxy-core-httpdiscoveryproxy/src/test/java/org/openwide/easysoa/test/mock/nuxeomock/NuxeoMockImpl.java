package org.openwide.easysoa.test.mock.nuxeomock;

import java.io.File;

import javax.ws.rs.core.Context;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.UriInfo;

import org.apache.log4j.Logger;
import org.openwide.easysoa.test.FullMockedHttpDiscoveryProxyTest;
import org.openwide.easysoa.test.Utilities;

public class NuxeoMockImpl implements NuxeoMock {

	/**
	 * Logger
	 */
	private static Logger logger = Logger.getLogger(NuxeoMockImpl.class.getName());
	
	@Override
	public String processNotificationRequest(String type) {
		// Returns only a OK response
		return "{\"result\": \"ok\"}";
	}

	@Override
	public String processAutomationRequest(UriInfo ui, Request request, String body) {
		// 2 Different response, depending the query ...
		// Get the body content and test
		System.out.println("Body = " + body);
		//Body = {"params":{"query":"SELECT * FROM Document WHERE ecm:path STARTSWITH '/default-domain/workspaces/' AND dc:title = 'show' AND ecm:currentLifeCycleState <> 'deleted' ORDER BY ecm:path"}}		
		//Body = {"params":{"query":"SELECT * FROM Document WHERE ecm:path STARTSWITH '/default-domain/workspaces/' AND ecm:currentLifeCycleState <> 'deleted' AND ecm:primaryType = 'Service' AND dc:title = 'meteo'"}}		
		
		if(body.contains("dc:title = 'meteo'")){
			return Utilities.readResponseFile("src/test/resources/nuxeoMockMessages/nuxeoResponseTestSoap.json");
		} else {
			return Utilities.readResponseFile("src/test/resources/nuxeoMockMessages/nuxeoResponseTestRest.json");
		}
	}

}
