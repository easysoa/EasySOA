package org.openwide.easysoa.test;

import org.json.JSONException;


/**
 * ServiceTestHelper for FullMocked mock setup
 * 
 * @author mdutoo
 *
 */
public class FullMockedServiceTestHelper extends ServiceTestHelperBase {

	@Override
	protected void waitForServices(int i) {
		// no need to sleep when mocked
	}

	@Override
	protected String cleanNuxeoRegistry(String urlPattern) throws JSONException {
		// no need to clean when mocked
		return null;
	}

}
