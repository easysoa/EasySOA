package org.easysoa.rest;

import org.easysoa.doctypes.AppliImpl;
import org.easysoa.doctypes.Service;
import org.easysoa.doctypes.ServiceAPI;
import org.easysoa.test.EasySOAFeatureBase;
import org.easysoa.test.rest.RestNotificationFactory.RestNotificationAPI;
import org.easysoa.test.rest.RestNotificationRequest;
import org.junit.Assume;
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
@Features({WebEngineFeature.class, EasySOAFeatureBase.class})
@Deploy({"org.nuxeo.runtime.jtajca",
		"org.nuxeo.runtime.management",
		"org.nuxeo.ecm.platform.login",
		"org.nuxeo.ecm.automation.core",
		"org.nuxeo.ecm.automation.features",
		"org.nuxeo.ecm.automation.server",
		"org.nuxeo.ecm.platform.query.api",
		"org.easysoa.demo.rest"
	})
@Jetty(port = 9980, config= "src/test/resources/jetty.xml")
//@RepositoryConfig(type=BackendType.H2, user = "Administrator", init=EasySOARepositoryInit.class)
@LocalDeploy({"org.easysoa.demo.rest:org/easysoa/tests/login-contrib.xml"/*,
	"org.easysoa.demo.rest:org/easysoa/tests/datasource-contrib.xml"*/})
public class DocumentCreationTest extends AbstractNotificationTest {

	@Inject
	CoreSession session;

	public DocumentCreationTest() throws Exception {
		super();
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
				.createNotification(RestNotificationAPI.APPLIIMPL);
		Assume.assumeNotNull(notification); // Assumes are a bit random ATM,
											// TODO improve them
		notification.setProperty(AppliImpl.PROP_TITLE, title);
		notification.setProperty(AppliImpl.PROP_URL, url);
		Assume.assumeTrue(notification.send());

		nuxeoAssert.assertDocumentExists(AppliImpl.DOCTYPE, url);
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
				.createNotification(RestNotificationAPI.SERVICEAPI);
		Assume.assumeNotNull(notification);
		notification.setProperty(ServiceAPI.PROP_TITLE, title)
				.setProperty(ServiceAPI.PROP_PARENTURL, parentUrl)
				.setProperty(ServiceAPI.PROP_URL, url);
		Assume.assumeTrue(notification.send());

		nuxeoAssert.assertDocumentExists(ServiceAPI.DOCTYPE, url);
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
				.createNotification(RestNotificationAPI.SERVICE);
		Assume.assumeNotNull(notification);
		notification.setProperty(Service.PROP_TITLE, title)
				.setProperty(Service.PROP_PARENTURL, parentUrl)
				.setProperty(Service.PROP_URL, url);
		Assume.assumeTrue(notification.send());

		nuxeoAssert.assertDocumentExists(Service.DOCTYPE, url);
	}
}
