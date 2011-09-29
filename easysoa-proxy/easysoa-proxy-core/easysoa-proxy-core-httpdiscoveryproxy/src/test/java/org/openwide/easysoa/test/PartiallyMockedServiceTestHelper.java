package org.openwide.easysoa.test;

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
