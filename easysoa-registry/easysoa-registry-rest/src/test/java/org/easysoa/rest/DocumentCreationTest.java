package org.easysoa.rest;

import org.easysoa.doctypes.AppliImpl;
import org.easysoa.doctypes.Service;
import org.easysoa.doctypes.ServiceAPI;
import org.easysoa.test.EasySOAFeatureBase;
import org.easysoa.test.rest.RestNotificationFactory.RestNotificationService;
import org.easysoa.test.rest.AbstractRestTest;
import org.easysoa.test.rest.RestNotificationRequest;
import org.junit.Assume;
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
 * Basic document creation tests.
 * 
 * @author mkalam-alami
 * 
 */
@RunWith(FeaturesRunner.class)
@Features({EasySOAFeatureBase.class, WebEngineFeature.class})
@Deploy("org.easysoa.registry.rest")
@Jetty(config="src/test/resources/jetty.xml", port=9980)
@LocalDeploy({"org.easysoa.registry.rest:OSGI-INF/login-contrib.xml"})
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

		RestNotificationRequest notification = notificationFactory
				.createNotification(RestNotificationService.APPLIIMPL);
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

		RestNotificationRequest notification = notificationFactory
				.createNotification(RestNotificationService.SERVICEAPI);
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

		RestNotificationRequest notification = notificationFactory
				.createNotification(RestNotificationService.SERVICE);
		Assume.assumeNotNull(notification);
		notification.setProperty(Service.PROP_TITLE, title)
				.setProperty(Service.PROP_PARENTURL, parentUrl)
				.setProperty(Service.PROP_URL, url);
		Assume.assumeNotNull(notification.send());

		assertDocumentExists(session, Service.DOCTYPE, url);
	}
}
