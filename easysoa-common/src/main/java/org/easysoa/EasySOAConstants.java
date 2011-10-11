package org.easysoa;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import org.apache.log4j.Logger;

/**
 * This class contains global EasySOA constants, mainly 
 * used to store the ports used by various components.
 * 
 * TODO: Change for a real configuration file.
 * 
 * @author mkalam-alami, jguillemotte
 *
 */
public class EasySOAConstants {

	private static Logger logger = Logger.getLogger(EasySOAConstants.class.getName());

    public final static int NUXEO_TEST_PORT = 6088;
    
    // HTTP discovery proxy
    public final static int HTTP_DISCOVERY_PROXY_PORT = 8082;
    public final static int HTTP_DISCOVERY_PROXY_DRIVER_PORT = 8084;
	public final static int TWITTER_MOCK_PORT = 8088;
	public final static int METEO_MOCK_PORT = 8085;
	//public final static int NUXEO_MOCK_PORT = 8087;
	
	// Scaffolding proxy
	public final static int REST_SOAP_PROXY_PORT = 7001;
	public final static int HTML_FORM_GENERATOR_PORT = 8090;
    public final static int HTML_FORM_GENERATOR_WEBSERVER_PORT = 8091;
	
    // Pure Air Flowers demo
    public final static int PAF_SERVICES_PORT = 9010;
    
    // Web
    public final static int WEB_PORT = 8083;
    
    private final static Map<String,Object> constantMap = new HashMap<String,Object>();
    
    public static final Object get(String constantName) {
    	return constantMap.get(constantName);
    }
    
    static {
    	for (Field constantField : EasySOAConstants.class.getFields()) {
			try {
	    		constantMap.put(constantField.getName(), constantField.get(null));
			} catch (Exception e) {
				logger.error("Error accessing EasySOAConstants field", e);
			}
    	}
    }
    
}
