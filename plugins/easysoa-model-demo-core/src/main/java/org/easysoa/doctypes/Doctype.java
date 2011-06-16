package org.easysoa.doctypes;

public class Doctype {

	public static final String PROP_TITLE = "title";
	public static final String PROP_DESCRIPTION = "description";

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

}