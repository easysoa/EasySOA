/**
 * EasySOA Proxy
 * Copyright 2011 Open Wide
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * 
 * Contact : easysoa-dev@groups.google.com
 */

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
