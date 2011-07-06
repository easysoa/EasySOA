package org.easysoa.rest;

import org.easysoa.doctypes.AppliImpl;
import org.easysoa.doctypes.Service;
import org.easysoa.doctypes.ServiceAPI;
import org.easysoa.test.RestNotificationFactory.RestNotificationAPI;
import org.easysoa.test.EasySOAFeature;
import org.easysoa.test.EasySOARepositoryInit;
import org.easysoa.test.RestNotificationRequest;
import org.junit.Assume;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.nuxeo.ecm.core.test.annotations.BackendType;
import org.nuxeo.ecm.core.test.annotations.RepositoryConfig;
import org.nuxeo.runtime.test.runner.Features;
import org.nuxeo.runtime.test.runner.FeaturesRunner;

/**
 * Basic document creation tests.
 * @author mkalam-alami
 *
 */
@RunWith(FeaturesRunner.class)
@Features(EasySOAFeature.class)
@RepositoryConfig(type=BackendType.H2, user = "Administrator", init=EasySOARepositoryInit.class)
public class DocumentCreationTest extends AbstractNotificationTest {
	
	public DocumentCreationTest() throws Exception {
		super(null); // TODO Inject
	}

	/**
	 * Creates an Appli Impl.
	 * @throws Exception
	 */
	@Test
	public void createAppliImpl() throws Exception {
		
		String url = "http://myApp.com/", title = "myApp";
		
		RestNotificationRequest notification = notificationFactory.createNotification(RestNotificationAPI.APPLIIMPL);
		Assume.assumeNotNull(notification); // TODO: Assumes are a bit random atm, improve them
		notification.setProperty(AppliImpl.PROP_TITLE, title);
		notification.setProperty(AppliImpl.PROP_URL, url);
		Assume.assumeTrue(notification.send());
		
		nuxeoAssert.assertDocumentExists(AppliImpl.DOCTYPE, url);
	}
	
	/**
	 * Creates a Service API.
	 * @throws Exception
	 */
	@Test
	public void createServiceAPI() throws Exception {
		
		String url = "api/", parentUrl = "http://myApp.com/", title = "myApi";
		
		RestNotificationRequest notification = notificationFactory.createNotification(RestNotificationAPI.SERVICEAPI);
		Assume.assumeNotNull(notification);
		notification.setProperty(ServiceAPI.PROP_TITLE, title)
				.setProperty(ServiceAPI.PROP_PARENTURL, parentUrl)
				.setProperty(ServiceAPI.PROP_URL, url);
		Assume.assumeTrue(notification.send());

		nuxeoAssert.assertDocumentExists(ServiceAPI.DOCTYPE, url);
	}
	
	/**
	 * Creates a Service.
	 * @throws Exception
	 */
	@Test
	public void createService() throws Exception {
		
		String url = "service", parentUrl = "api/", title = "myService";
		
		RestNotificationRequest notification = notificationFactory.createNotification(RestNotificationAPI.SERVICE);
		Assume.assumeNotNull(notification);
		notification.setProperty(Service.PROP_TITLE, title)
				.setProperty(Service.PROP_PARENTURL, parentUrl)
				.setProperty(Service.PROP_URL, url);
		Assume.assumeTrue(notification.send());

		nuxeoAssert.assertDocumentExists(Service.DOCTYPE, url);
	}
}
