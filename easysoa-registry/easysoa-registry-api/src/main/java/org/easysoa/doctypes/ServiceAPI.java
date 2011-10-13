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
 * Contact : easysoa-dev@groups.google.com
 */

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
    private static Object propertyListSync = new Object();
    
    /**
     * Returns a map containing all properties from the ServiceAPI schema,
     * with a short description.
     */
    public static Map<String, String> getPropertyList() {
        synchronized (propertyListSync) { // Ensures initialization ended before accessing the list
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
