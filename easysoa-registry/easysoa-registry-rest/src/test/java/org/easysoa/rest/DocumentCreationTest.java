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
 * Contact : easysoa-dev@googlegroups.com
 */

package org.easysoa.rest;

import org.easysoa.EasySOAConstants;
import org.easysoa.doctypes.AppliImpl;
import org.easysoa.doctypes.Service;
import org.easysoa.doctypes.ServiceAPI;
import org.easysoa.rest.RestNotificationFactory.RestDiscoveryService;
import org.easysoa.test.AbstractRestTest;
import org.easysoa.test.EasySOACoreFeature;
import org.junit.Assume;
import org.junit.Before;
import org.junit.Ignore;
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
 * Basic document creation tests.
 * 
 * @author mkalam-alami
 * 
 */
@RunWith(FeaturesRunner.class)
@Features({EasySOACoreFeature.class, WebEngineFeature.class})
@Jetty(config="src/test/resources/jetty.xml", port=EasySOAConstants.NUXEO_TEST_PORT)
@Deploy("org.easysoa.registry.rest")
@LocalDeploy({"org.easysoa.registry.rest:OSGI-INF/login-contrib.xml"})
@Ignore 
public class DocumentCreationTest extends AbstractRestTest {

	@Inject CoreSession session;

	@Before
	public void setUp() throws Exception {
		setUp(session, "src/test/resources/targetednuxeo.properties");
	}
	
	/**
	 * Creates an Appli Impl.
	 * 
	 * @throws Exception
	 */
	@Test
	public void testCreateAppliImpl() throws Exception {
		String url = "http://myApp.com/", title = "myApp";

		RestNotificationRequest notification = getRestNotificationFactory()
				.createNotification(RestDiscoveryService.APPLIIMPL);
		Assume.assumeNotNull(notification); // TODO Assumes are a bit random ATM, improve them
		notification.setProperty(AppliImpl.PROP_TITLE, title);
		notification.setProperty(AppliImpl.PROP_URL, url);
		Assume.assumeNotNull(notification.send());

		assertDocumentExists(session, AppliImpl.DOCTYPE, url);
	}

	/**
	 * Creates a Service API.
	 * 
	 * @throws Exception
	 */
	@Test
	public void testCreateServiceAPI() throws Exception {
		String url = "api/", parentUrl = "http://myApp.com/", title = "myApi";

		RestNotificationRequest notification = getRestNotificationFactory()
				.createNotification(RestDiscoveryService.SERVICEAPI);
		Assume.assumeNotNull(notification);
		notification.setProperty(ServiceAPI.PROP_TITLE, title)
				.setProperty(ServiceAPI.PROP_PARENTURL, parentUrl)
				.setProperty(ServiceAPI.PROP_URL, url);
		Assume.assumeNotNull(notification.send());

		assertDocumentExists(session, ServiceAPI.DOCTYPE, url);
	}

	/**
	 * Creates a Service.
	 * 
	 * @throws Exception
	 */
	@Test
	public void testCreateService() throws Exception {
		String url = "service", parentUrl = "api/", title = "myService";

		RestNotificationRequest notification = getRestNotificationFactory()
				.createNotification(RestDiscoveryService.SERVICE);
		Assume.assumeNotNull(notification);
		notification.setProperty(Service.PROP_TITLE, title)
				.setProperty(Service.PROP_PARENTURL, parentUrl)
				.setProperty(Service.PROP_URL, url);
		Assume.assumeNotNull(notification.send());

		assertDocumentExists(session, Service.DOCTYPE, url);
	}
}
