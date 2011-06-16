package org.easysoa.test.rest.tools;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class ConfigurationLoader {
	
	private static final Log log = LogFactory.getLog(ConfigurationLoader.class);
	
	private static RestNotificationFactory factory = null;
	
	public static RestNotificationFactory getRestNotificationFactory() throws IOException {
		
		if (factory == null) {
		
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
				factory = new RestNotificationFactory(RestNotificationFactory.NUXEO_URL_LOCALHOST);
			}
			factory = new RestNotificationFactory("http://"+nuxeoHost+":"+nuxeoPort+"/nuxeo");
			
		}
		
		return factory;
	}

}
