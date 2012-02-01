/**
 * EasySOA Registry
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

package org.easysoa.doctypes;

import java.util.HashMap;
import java.util.Map;

/**
 * Mirror of the properties defined in the XML contributions. Defines all the
 * Service related data.
 * 
 * @author mkalam-alami
 * 
 */
public class Service extends EasySOADoctype {

    public static final String DOCTYPE = "Service";
    public static final String SCHEMA = "servicedef";
    public static final String SCHEMA_PREFIX = "serv:";

    public static final String PROP_URL = "url";
    public static final String PROP_URLTEMPLATE = "urlTemplate";
    public static final String PROP_LIGHTURL = "lightUrl";
    public static final String PROP_CALLCOUNT = "callcount";
    public static final String PROP_PARTICIPANTS = "participants";
    public static final String PROP_HTTPMETHOD = "httpMethod";
    public static final String PROP_CONTENTTYPEIN = "contentTypeIn";
    public static final String PROP_CONTENTTYPEOUT = "contentTypeOut";
    public static final String PROP_FILEURL = "fileUrl"; // TODO: Switch to EasySOADoctype?
    public static final String PROP_REFERENCESERVICE = "referenceService";
    public static final String PROP_REFERENCESERVICEORIGIN = "referenceServiceOrigin";
    public static final String PROP_WSDLSERVICENAME = "wsdlServiceName";
    public static final String PROP_WSDLNAMESPACE = "wsdlNamespace";
    public static final String PROP_ISVALIDATED = "isValidated";
    public static final String PROP_VALIDATIONSTATE = "validationState";
    public static final String SUBPROP_VALIDATORNAME = "validatorName";
    public static final String SUBPROP_ISVALIDATED = "isValidated";
    public static final String SUBPROP_VALIDATIONLOG = "validationLog";

    public static final String PROP_PARENTURL = "parentUrl";

    private static Map<String, String> propertyList = null;
    private static Object propertyListSync = new Object();

    /**
     * Returns a map containing all properties from the Service.SCHEMA schema,
     * with a short description.
     */
    public static Map<String, String> getPropertyList() {
        synchronized (propertyListSync) { // Ensures initialization ended before accessing the list
            if (propertyList == null) {
                propertyList = new HashMap<String, String>();
                propertyList.put(PROP_URL, "(mandatory) Service URL");
                propertyList.put(PROP_URLTEMPLATE, "A template to build the URL from according to the environment");
                propertyList.put(PROP_CALLCOUNT, "Times the service has been called since last notification");
                propertyList.put(PROP_PARTICIPANTS, "Consumers & providers of the service");
                propertyList.put(PROP_HTTPMETHOD, "POST, GET...");
                propertyList.put(PROP_CONTENTTYPEIN, "HTTP content type of the request body");
                propertyList.put(PROP_CONTENTTYPEOUT, "HTTP content type of the result body");
                propertyList.put(PROP_FILEURL, "The URL of a file to attach to the document");
                propertyList.put(PROP_PARENTURL, "(mandatory) Service API URL");
            }
            return propertyList;
        }
    }
}
