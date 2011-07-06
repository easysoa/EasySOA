package org.easysoa.rest;

import java.io.FileInputStream;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.easysoa.test.NuxeoAssertionHelper;
import org.easysoa.test.RestNotificationFactory;
import org.nuxeo.ecm.core.api.CoreSession;

/**
 * Class to be extended for the creation of notification test cases.
 * @author mkalam-alami
 *
 */
public abstract class AbstractNotificationTest {
	
	private static final Log log = LogFactory.getLog(AbstractNotificationTest.class);

	protected NuxeoAssertionHelper nuxeoAssert = null;
	protected RestNotificationFactory notificationFactory = null;

	private String nuxeoUrl = null;
	
	public AbstractNotificationTest(CoreSession session) throws Exception {
		
		// Load properties
		FileInputStream isProps = new FileInputStream("src/test/resources/targetednuxeo.properties");
		Properties props = new Properties();
		props.load(isProps);
		isProps.close();
		
		// Read properties
		String nuxeoHost = props.getProperty("nuxeoHost");
		String nuxeoPort = props.getProperty("nuxeoPort");
		boolean useExistingNuxeo = props.getProperty("useExistingNuxeo").equals("true");
		
		// Build URL
		if (nuxeoHost == null || nuxeoPort == null) {
			log.warn("Invalid Nuxeo location, using default: "+
					RestNotificationFactory.NUXEO_URL_LOCALHOST);
			nuxeoUrl = RestNotificationFactory.NUXEO_URL_LOCALHOST;
		}
		else {
			nuxeoUrl = "http://"+nuxeoHost+":"+nuxeoPort+"/nuxeo";
		}
		
		// Create testing objects
		notificationFactory = new RestNotificationFactory(nuxeoUrl);
		if (useExistingNuxeo) {
			nuxeoAssert = new NuxeoAssertionHelper(nuxeoUrl);
		}
		else {
			nuxeoAssert = new NuxeoAssertionHelper(session);
		}
	}

}
