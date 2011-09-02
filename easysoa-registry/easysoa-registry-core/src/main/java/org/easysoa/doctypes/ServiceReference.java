package org.easysoa.doctypes;

import java.util.HashMap;
import java.util.Map;

/**
 * Mirror of the properties defined in the XML contributions. Defines all the
 * Service Reference related data.
 * 
 * @author mkalam-alami
 * 
 */
public class ServiceReference extends EasySOADoctype {

    public static final String DOCTYPE = "ServiceReference";
    public static final String SCHEMA = "serviceref";
    public static final String SCHEMA_PREFIX = "sref:";

    public static final String PROP_REFURL = "refUrl";
    public static final String PROP_REFPATH = "refPath";

    public static final String PROP_PARENTURL = "parentUrl";

    private static Map<String, String> propertyList = null;

    /**
     * Returns a map containing all properties from the ServiceReference.SCHEMA
     * schema, with a short description.
     */
    public static Map<String, String> getPropertyList() {
        synchronized (DOCTYPE) { // Ensures initialization ended before accessing the list
            if (propertyList == null) {
                propertyList = new HashMap<String, String>();
                propertyList.put(PROP_REFURL, "Referenced Service URL");
                propertyList.put(PROP_REFPATH, "Path of Referenced Service in registry if known");
                propertyList.put(PROP_PARENTURL, "(mandatory) The parent application URL.");
            }
            return propertyList;
        }
    }
}
