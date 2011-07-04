package org.easysoa.doctypes;

import java.util.HashMap;
import java.util.Map;

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
	public static final String PROP_FILEURL = "fileUrl";

	private static Map<String, String> commonPropertyList = null;
	private static Map<String, String> dublinCorePropertyList = null;

	public static Map<String, String> getCommonPropertyList() {
		if (commonPropertyList == null) {
			commonPropertyList = new HashMap<String, String>();
			commonPropertyList.put(PROP_DTBROWSING, "Notes about browsing-specific notifications. Informs the document of the notification source.");
			commonPropertyList.put(PROP_DTMONITORING, "Notes about monitoring-specific notifications. Informs the document of the notification source.");
			commonPropertyList.put(PROP_DTIMPORT, "Notes about import-specific notifications. Informs the document of the notification source.");
			commonPropertyList.put(PROP_ARCHIPATH, "Reference Path in architecture if known.");
			commonPropertyList.put(PROP_ARCHILOCALNAME, "Reference local name in architecture if known.");
			commonPropertyList.put(PROP_FILEURL, "The URL of a file to attach to the document.");
		}
		return commonPropertyList;
	}
	
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
	
	public static String getSchema(String doctype) {
		if (doctype.equals(AppliImpl.DOCTYPE))
			return AppliImpl.SCHEMA;
		if (doctype.equals(ServiceAPI.DOCTYPE))
			return ServiceAPI.SCHEMA;
		if (doctype.equals(Service.DOCTYPE))
			return Service.SCHEMA;
		return null;
	}

	public static String getSchemaPrefix(String doctype) {
		if (doctype.equals(AppliImpl.DOCTYPE))
			return AppliImpl.SCHEMA_PREFIX;
		if (doctype.equals(ServiceAPI.DOCTYPE))
			return ServiceAPI.SCHEMA_PREFIX;
		if (doctype.equals(Service.DOCTYPE))
			return Service.SCHEMA_PREFIX;
		return null;
	}
	
	public static void loadMetadataProperties() {
		// TODO Auto-generated method stub
		
	}

}