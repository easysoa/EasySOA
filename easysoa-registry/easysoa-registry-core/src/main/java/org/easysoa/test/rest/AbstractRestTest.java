package org.easysoa.test.rest;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import java.io.FileInputStream;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.easysoa.doctypes.AppliImpl;
import org.easysoa.doctypes.Service;
import org.easysoa.doctypes.ServiceAPI;
import org.easysoa.doctypes.ServiceReference;
import org.easysoa.services.DocumentService;
import org.easysoa.test.tools.AutomationHelper;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.runtime.api.Framework;

/**
 * Class to be extended for the creation of REST APIs test cases.
 * @author mkalam-alami
 *
 */
public abstract class AbstractRestTest {
	
	private static final Log log = LogFactory.getLog(AbstractRestTest.class);

	protected static AutomationHelper automation = null;
	protected static RestNotificationFactory notificationFactory = null;
	protected static boolean useEmbeddedNuxeo;
	
	private static Object notificationFactorySync = new Object();

	protected static void setUp(CoreSession session, String targetedNuxeoPropsPath) throws Exception {
		
	    synchronized (notificationFactorySync) {
	    
		// Run only once
		if (notificationFactory != null)
			return;
		
		// Load properties
		/**
		 * Nuxeo props file exemple:
			"useExistingNuxeo = true
			existingNuxeoHost = localhost
			existingNuxeoPort = 8080
			embeddedNuxeoPort = 9980" 
		 */
		FileInputStream isProps = new FileInputStream(targetedNuxeoPropsPath);
		Properties props = new Properties();
		props.load(isProps);
		isProps.close();
		
		// Read properties
		String externalNuxeoHost = props.getProperty("externalNuxeoHost");
		String externalNuxeoPort = props.getProperty("externalNuxeoPort");
		String embeddedNuxeoPort = props.getProperty("embeddedNuxeoPort");
		
		String useEmbeddedNuxeoString = props.getProperty("useEmbeddedNuxeo");
		if (useEmbeddedNuxeoString != null && useEmbeddedNuxeoString.equals("false")) {
			log.info("Running test on external Nuxeo");
			useEmbeddedNuxeo = false;
		}
		else {
			log.info("Running test on embedded Nuxeo");
			useEmbeddedNuxeo = true;
		}
		
		// Create testing objects
		String nuxeoUrl = null;
		if (useEmbeddedNuxeo) {
			nuxeoUrl = "http://localhost:"+embeddedNuxeoPort;
		}
		else {
			if (externalNuxeoHost == null || externalNuxeoPort == null) {
				log.warn("Invalid Nuxeo location, using default: "+
						RestNotificationFactory.NUXEO_URL_LOCALHOST);
				nuxeoUrl = RestNotificationFactory.NUXEO_URL_LOCALHOST;
			}
			else {
				nuxeoUrl = "http://"+externalNuxeoHost+":"+externalNuxeoPort+"/nuxeo/site";
			}
			// Could also be deployed with the existing Nuxeo,
			// if the automation bundles were correctly deployed & configured.
			automation = new AutomationHelper(nuxeoUrl);
		}
		notificationFactory = new RestNotificationFactory(nuxeoUrl);

        }
	}

	protected void assertDocumentExists(CoreSession session, String doctype, String url) throws Exception {
		if (useEmbeddedNuxeo && session != null) {
			DocumentService docService = Framework.getService(DocumentService.class);
			DocumentModel model = null;
			// TODO DocumentService refactoring
			if (AppliImpl.DOCTYPE.equals(doctype))
				model = docService.findAppliImpl(session, url);
			else if (ServiceAPI.DOCTYPE.equals(doctype))
				model = docService.findServiceApi(session, url);
			else if (Service.DOCTYPE.equals(doctype))
				model = docService.findService(session, url);
			else if (ServiceReference.DOCTYPE.equals(doctype))
				model = docService.findServiceReference(session, url);
			assertNotNull(model);
		}
		else if (automation != null) {
			assertFalse(automation.findDocumentByUrl(doctype, url).isEmpty());
		}
		else {
			fail("Cannot access repository");
		}
	}
}
