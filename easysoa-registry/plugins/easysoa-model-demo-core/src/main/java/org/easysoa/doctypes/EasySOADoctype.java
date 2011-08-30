package org.easysoa.doctypes;

import java.util.HashMap;
import java.util.Map;

/**
 * Mirror of the properties defined in the XML contributions. Defines all data
 * common to every SOA document type.
 * 
 * @author mkalam-alami
 * 
 */
public class EasySOADoctype {

    public static final String SCHEMA_COMMON = "soacommon";
    public static final String SCHEMA_COMMON_PREFIX = "soa:";
    public static final String SCHEMA_DUBLINCORE = "dublincore";

    // Dublin Core properties
    public static final String PROP_TITLE = "title";
    public static final String PROP_DESCRIPTION = "description";

    // SOA Common properties
    public static final String PROP_DTBROWSING = "discoveryTypeBrowsing";
    public static final String PROP_DTMONITORING = "discoveryTypeMonitoring";
    public static final String PROP_DTIMPORT = "discoveryTypeImport";
    public static final String PROP_ARCHIPATH = "archiPath";
    public static final String PROP_ARCHILOCALNAME = "archiLocalName";

    private static Map<String, String> commonPropertyList = null;
    private static Map<String, String> dublinCorePropertyList = null;

    /**
     * Returns a map containing all properties from the SCHEMA_COMMON schema,
     * with a short description.
     */
    public static Map<String, String> getCommonPropertyList() {
        if (commonPropertyList == null) {
            commonPropertyList = new HashMap<String, String>();
            commonPropertyList.put(PROP_ARCHIPATH, "Reference Path in architecture if known.");
            commonPropertyList.put(PROP_ARCHILOCALNAME, "Reference local name in architecture if known.");
            commonPropertyList.put(PROP_DTBROWSING, "Notes about browsing-specific notifications." +
                    " Informs the document of the notification source.");
            commonPropertyList.put(PROP_DTMONITORING, "Notes about monitoring-specific notifications." +
                    " Informs the document of the notification source.");
            commonPropertyList.put(PROP_DTIMPORT, "Notes about import-specific notifications." +
                    " Informs the document of the notification source.");
        }
        return commonPropertyList;
    }

    /**
     * Returns a map containing all properties from the SCHEMA_DUBLINCORE
     * schema, with a short description.
     * 
     * @return
     */
    public static Map<String, String> getDublinCorePropertyList() {
        if (dublinCorePropertyList == null) {
            dublinCorePropertyList = new HashMap<String, String>();
            dublinCorePropertyList.put(PROP_TITLE, "The name of the document.");
            dublinCorePropertyList.put(PROP_DESCRIPTION, "A short description.");
        }
        return dublinCorePropertyList;
    }

    // TODO: Put url (+parentUrl ?) in a common schema, so that these
    // won't be needed anymore.

    /**
     * Returns the name of the specific schema of a document given its type.
     */
    public static String getSchema(String doctype) {
        if (doctype.equals(AppliImpl.DOCTYPE))
            return AppliImpl.SCHEMA;
        if (doctype.equals(ServiceAPI.DOCTYPE))
            return ServiceAPI.SCHEMA;
        if (doctype.equals(Service.DOCTYPE))
            return Service.SCHEMA;
        return null;
    }

    /**
     * Returns the name of the specific schema of a document given its type, in
     * the form of a property prefix (ex: "app:")
     */
    public static String getSchemaPrefix(String doctype) {
        if (doctype.equals(AppliImpl.DOCTYPE))
            return AppliImpl.SCHEMA_PREFIX;
        if (doctype.equals(ServiceAPI.DOCTYPE))
            return ServiceAPI.SCHEMA_PREFIX;
        if (doctype.equals(Service.DOCTYPE))
            return Service.SCHEMA_PREFIX;
        return null;
    }

}