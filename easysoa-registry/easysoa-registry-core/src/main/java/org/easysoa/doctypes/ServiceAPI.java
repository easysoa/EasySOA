package org.easysoa.doctypes;

import java.util.HashMap;
import java.util.Map;

/**
 * Mirror of the properties defined in the XML contributions.
 * Defines all the Service API related data.
 * 
 * @author mkalam-alami
 *
 */
public class ServiceAPI extends EasySOADoctype {

	public static final String DOCTYPE = "ServiceAPI";
	public static final String SCHEMA = "serviceapidef";
	public static final String SCHEMA_PREFIX = "api:";
	
	public static final String PROP_URL = "url";
	public static final String PROP_APPLICATION = "application";
	public static final String PROP_PROTOCOLS = "protocols";
	
	public static final String PROP_PARENTURL = "parentUrl";

	private static Map<String, String> propertyList = null;
	
	/**
     * Returns a map containing all properties from the ServiceAPI schema,
     * with a short description.
     */
	public static Map<String, String> getPropertyList() {
        synchronized (DOCTYPE) { // Ensures initialization ended before accessing the list
    		if (propertyList == null) {
    			propertyList = new HashMap<String, String>(); 
    			propertyList.put(PROP_URL, "(mandatory) Service API url (WSDL address, parent path...).");
    			propertyList.put(PROP_APPLICATION, "The related business application.");
    			propertyList.put(PROP_PROTOCOLS, "The supported protocols.");
    			propertyList.put(PROP_PARENTURL, "The parent URL, which is either another service API, or the service root.");
    		}
    		return propertyList;
        }
	}
	
}
