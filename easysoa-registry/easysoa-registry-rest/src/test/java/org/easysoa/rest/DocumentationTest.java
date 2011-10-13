/**
 * EasySOA Registry
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

package org.easysoa.rest;

import org.easysoa.EasySOAConstants;
import org.easysoa.doctypes.EasySOADoctype;
import org.easysoa.doctypes.Service;
import org.easysoa.rest.RestNotificationFactory.RestNotificationService;
import org.easysoa.test.EasySOACoreFeature;
import org.easysoa.test.rest.AbstractRestTest;
import org.json.JSONObject;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.webengine.test.WebEngineFeature;
import org.nuxeo.runtime.test.runner.Deploy;
import org.nuxeo.runtime.test.runner.Features;
import org.nuxeo.runtime.test.runner.FeaturesRunner;
import org.nuxeo.runtime.test.runner.Jetty;
import org.nuxeo.runtime.test.runner.LocalDeploy;

import com.google.inject.Inject;

/**
 * Single test to check documentation requests work as expected.
 * 
 * @author mkalam-alami
 * 
 */
@RunWith(FeaturesRunner.class)
@Features({EasySOACoreFeature.class, WebEngineFeature.class})
@Deploy({
	"org.easysoa.registry.rest"
})
@Jetty(config="src/test/resources/jetty.xml", port=EasySOAConstants.NUXEO_TEST_PORT)
@LocalDeploy({"org.easysoa.registry.rest:OSGI-INF/login-contrib.xml"})
public class DocumentationTest extends AbstractRestTest {

	@Inject CoreSession session;

	@Before
	public void setUp() throws Exception {
		setUp(session, "src/test/resources/targetednuxeo.properties");
	}
	
	@Test
	public void testNotificationDocumentation() throws Exception {
		// Create request
		RestNotificationRequest request = notificationFactory.createNotification(RestNotificationService.SERVICE, "GET");
		JSONObject doc = request.send();
		
		// Check documentation contents
		Assert.assertTrue(doc != null && doc.has("parameters"));
		JSONObject parameters = (JSONObject) doc.get("parameters");
        Assert.assertTrue(parameters.has(Service.PROP_URL));
        Assert.assertTrue(parameters.has(EasySOADoctype.PROP_TITLE));
	}

}
