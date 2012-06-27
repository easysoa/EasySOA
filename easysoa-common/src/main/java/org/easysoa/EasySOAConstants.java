/**
 * EasySOA Common
 * Copyright 2011 Open Wide
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * 
 * Contact : easysoa-dev@googlegroups.com
 */

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

	// Service registry
    public static final int NUXEO_PORT = 8080;
    public static final int NUXEO_TEST_PORT = 6088;
    
    // HTTP discovery proxy
    public static final int HTTP_DISCOVERY_PROXY_PORT = 8082;
    public static final int HTTP_DISCOVERY_PROXY_DRIVER_PORT = 8084;
	public static final int TWITTER_MOCK_PORT = 8088;
	public static final int METEO_MOCK_PORT = 8085;
	public static final int EXCHANGE_RECORD_REPLAY_SERVICE_PORT = 8086; 
	//public static final int NUXEO_MOCK_PORT = 8087;
	
	// Scaffolding proxy
	public static final int REST_SOAP_PROXY_PORT = 7001;
	//public static final int HTML_FORM_GENERATOR_PORT = 8090;
	public static final int HTML_FORM_GENERATOR_PORT = 18000;

    // Trip demo
    public static final int TRIP_SERVICES_PORT = 9000;
    public static final int TRIP_BACKUP_SERVICES_PORT = 9020;
    
    // Pure Air Flowers demo
    public static final int PAF_SERVICES_PORT = 9010;
    
    // Web
    public static final int WEB_PORT = 8083;
    
    private static final Map<String,Object> CONSTANT_MAP = new HashMap<String,Object>();
    static {
    	for (Field constantField : EasySOAConstants.class.getFields()) {
			try {
	    		CONSTANT_MAP.put(constantField.getName(), constantField.get(null));
			} catch (Exception e) {
				logger.error("Error accessing EasySOAConstants field", e);
			}
    	}
    }
    
    /**
     * Returns any constant value from this class
     * @param constantName The field name as a string
     */
    public static final Object get(String constantName) {
        return CONSTANT_MAP.get(constantName);
    }
    
    // Hide constructor
    private EasySOAConstants() {}
    
}
