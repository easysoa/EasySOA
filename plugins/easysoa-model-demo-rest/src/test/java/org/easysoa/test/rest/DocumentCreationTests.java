package org.easysoa.test.rest;

import java.io.IOException;

import org.easysoa.doctypes.AppliImpl;
import org.easysoa.doctypes.Service;
import org.easysoa.doctypes.ServiceAPI;
import org.easysoa.test.rest.tools.ConfigurationLoader;
import org.easysoa.test.rest.tools.RESTNotification;
import org.easysoa.test.rest.tools.RestNotificationFactory;
import org.easysoa.test.rest.tools.RestNotificationFactory.RestNotificationAPI;
import org.junit.BeforeClass;
import org.junit.Test;

public class DocumentCreationTests {

	private static RestNotificationFactory factory;
	
	@BeforeClass
	public static void setUp() throws IOException {
		factory = ConfigurationLoader.getRestNotificationFactory();
	}
	
	/**
	 * Creates an Appli Impl.
	 * @throws Exception
	 */
	@Test
	public void createAppliImpl() throws Exception {
		RESTNotification notification = factory.createNotification(RestNotificationAPI.APPLIIMPL);
		notification.setProperty(AppliImpl.PROP_TITLE, "myApp");
		notification.setProperty(AppliImpl.PROP_URL, "http://myApp.com/");
		notification.send();
	}
	
	/**
	 * Creates a Service API.
	 * @throws Exception
	 */
	@Test
	public void creatServiceAPI() throws Exception {
		RESTNotification notification = factory.createNotification(RestNotificationAPI.SERVICEAPI);
		notification.setProperty(ServiceAPI.PROP_TITLE, "myApi");
		notification.setProperty(ServiceAPI.PROP_PARENTURL, "http://myApp.com/");
		notification.setProperty(ServiceAPI.PROP_URL, "api/");
		notification.send();
	}
	
	/**
	 * Creates a Service.
	 * @throws Exception
	 */
	@Test
	public void createService() throws Exception {
		RESTNotification notification = factory.createNotification(RestNotificationAPI.APPLIIMPL);
		notification.setProperty(Service.PROP_TITLE, "myService");
		notification.setProperty(Service.PROP_PARENTURL, "api/");
		notification.setProperty(Service.PROP_URL, "service");
		notification.send();
	}
}
