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
 * Contact : easysoa-dev@googlegroups.com
 */

package org.openwide.easysoa.test.helpers;

import static org.junit.Assert.assertEquals;

import org.easysoa.EasySOAConstants;
import org.json.JSONException;


/**
 * ServiceTestHelper for PartiallyMocked mock setup
 * 
 * @author mdutoo
 *
 */
public class PartiallyMockedServiceTestHelper extends ServiceTestHelperBase {

	@Override
	protected void waitForServices(int i) throws InterruptedException {
		Thread.sleep(5000); // wait for actual nuxeo registry
	}

	@Override
	protected String cleanNuxeoRegistry(String urlPattern) throws JSONException {
    	String nuxeoResponse = cleanRemoteNuxeoRegistry("%" + EasySOAConstants.TWITTER_MOCK_PORT + "%");
    	assertEquals("{\n  \"entity-type\": \"documents\",\n  \"entries\": []\n}", nuxeoResponse);
		return nuxeoResponse;
	}

}
