package org.openwide.easysoa.test.mock.nuxeomock;

import javax.ws.rs.core.Request;
import javax.ws.rs.core.UriInfo;
import org.apache.log4j.Logger;
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
		if(body.contains("dc:title = 'meteo'")){
			return Utilities.readResponseFile("src/test/resources/nuxeoMockMessages/nuxeoResponseTestSoap.json");
		} else {
			return Utilities.readResponseFile("src/test/resources/nuxeoMockMessages/nuxeoResponseTestRest.json");
		}
	}

}
