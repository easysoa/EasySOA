package org.easysoa.rest;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.easysoa.test.AutomationHelper;
import org.easysoa.test.RestNotificationFactory;
import org.junit.Assume;

/**
 * Class to be extended for the creation of notification test cases.
 * @author mkalam-alami
 *
 */
public abstract class AbstractNotificationTest {
	
	private static final Log log = LogFactory.getLog(AbstractNotificationTest.class);

	protected RestNotificationFactory notificationFactory = null;
	protected AutomationHelper automation = null;

	private String nuxeoUrl = null;
	
	public AbstractNotificationTest() throws Exception {
		
		// Load properties
		FileInputStream isProps = new FileInputStream("src/test/resources/targetednuxeo.properties");
		Properties props = new Properties();
		props.load(isProps);
		isProps.close();
		
		// Read properties
		if (Boolean.parseBoolean(props.getProperty("useFakeNuxeo")))
			throw new UnsupportedOperationException("Not implemented yet!"); // TODO
		String nuxeoHost = props.getProperty("nuxeoHost");
		String nuxeoPort = props.getProperty("nuxeoPort");
		
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
		try {
			automation = new AutomationHelper(nuxeoUrl);
		}
		catch (Exception e) {
			automation = null;
		}
	}
	
	public AutomationHelper getAutomationHelper() throws IOException {
		return automation;
	}
	
	public RestNotificationFactory getRestNotificationFactory() throws IOException {
		return notificationFactory;
	}
	

}
