package org.easysoa.doctypes;

import java.util.HashMap;
import java.util.Map;

/**
 * Mirror of the properties defined in the XML contributions.
 * Defines all the Appli Impl. related data.
 * 
 * @author mkalam-alami
 *
 */
public class AppliImpl extends EasySOADoctype {

	public static final String DOCTYPE = "Workspace";
	public static final String SCHEMA = "appliimpldef";
	public static final String SCHEMA_PREFIX = "app:";
	
	public static final String DEFAULT_ENVIRONMENT = "Production";
	public static final String DEFAULT_APPLIIMPL_TITLE = "Default application";
	public static final String DEFAULT_APPLIIMPL_URL = "(Unknown)";
	
	public static final String PROP_URL = "url";
	public static final String PROP_UIURL = "uiUrl";
	public static final String PROP_SOURCESURL = "sourcesUrl";
	public static final String PROP_SERVER = "server";
	public static final String PROP_PROVIDER = "provider";
	public static final String PROP_ENVIRONMENT = "environment";
	public static final String PROP_TECHNOLOGY = "technology";
	public static final String PROP_STANDARD = "standard";
	
	public static final String PROP_SERVERENTRY = "serverEntry"; // Internal
	
	private static Map<String, String> propertyList = null;
    private static Object propertyListSync = new Object();
	
	/**
     * Returns a map containing all properties from the AppliImpl.SCHEMA schema,
     * with a short description.
     * @return
     */
	public static Map<String, String> getPropertyList() {
		synchronized (propertyListSync) {
			propertyList = new HashMap<String, String>(); 
			propertyList.put(PROP_URL, "(mandatory) Services root.");
			propertyList.put(PROP_UIURL, "Application GUI entry point.");
			propertyList.put(PROP_SOURCESURL, "Source code access.");
			propertyList.put(PROP_SERVER, "IP of the server.");
			propertyList.put(PROP_PROVIDER, "Company that provides these services.");
			propertyList.put(PROP_ENVIRONMENT, "The application environment (production, development...)");
			propertyList.put(PROP_TECHNOLOGY, "Services implementation technology.");
			propertyList.put(PROP_STANDARD, "Protocol standard if applicable.");
		}
		return propertyList;
	}
}