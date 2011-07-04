package org.easysoa.rest;

import static org.junit.Assert.assertFalse;

import org.easysoa.doctypes.AppliImpl;
import org.easysoa.doctypes.Service;
import org.easysoa.doctypes.ServiceAPI;
import org.easysoa.test.RestNotificationFactory.RestNotificationAPI;
import org.easysoa.test.RestNotificationRequest;
import org.junit.Assume;
import org.junit.Test;

/**
 * Basic document creation tests.
 * @author mkalam-alami
 *
 */
public class DocumentCreationTest extends AbstractNotificationTest {
	
	public DocumentCreationTest() throws Exception {
		super();
	}

	/**
	 * Creates an Appli Impl.
	 * @throws Exception
	 */
	@Test
	public void createAppliImpl() throws Exception {
		
		String url = "http://myApp.com/", title = "myApp";
		
		RestNotificationRequest notification = notificationFactory.createNotification(RestNotificationAPI.APPLIIMPL);
		notification.setProperty(AppliImpl.PROP_TITLE, title);
		notification.setProperty(AppliImpl.PROP_URL, url);
		Assume.assumeTrue(notification.send());
		
		assertFalse(automation.findDocumentByUrl(AppliImpl.DOCTYPE, url).isEmpty());
	}
	
	/**
	 * Creates a Service API.
	 * @throws Exception
	 */
	@Test
	public void createServiceAPI() throws Exception {
		
		String url = "api/", parentUrl = "http://myApp.com/", title = "myApi";
		
		RestNotificationRequest notification = notificationFactory.createNotification(RestNotificationAPI.SERVICEAPI);
		notification.setProperty(ServiceAPI.PROP_TITLE, title)
				.setProperty(ServiceAPI.PROP_PARENTURL, parentUrl)
				.setProperty(ServiceAPI.PROP_URL, url);
		notification.send();
		
		assertFalse(automation.findDocumentByUrl(ServiceAPI.DOCTYPE, url).isEmpty());
	}
	
	/**
	 * Creates a Service.
	 * @throws Exception
	 */
	@Test
	public void createService() throws Exception {
		
		String url = "service", parentUrl = "api/", title = "myService";
		
		RestNotificationRequest notification = notificationFactory.createNotification(RestNotificationAPI.SERVICE);
		notification.setProperty(Service.PROP_TITLE, title)
				.setProperty(Service.PROP_PARENTURL, parentUrl)
				.setProperty(Service.PROP_URL, url);
		notification.send();

		assertFalse(automation.findDocumentByUrl(Service.DOCTYPE, url).isEmpty());
	}
}
