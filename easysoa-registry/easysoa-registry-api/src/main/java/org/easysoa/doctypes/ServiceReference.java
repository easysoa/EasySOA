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
    private static Object propertyListSync = new Object();

    /**
     * Returns a map containing all properties from the ServiceReference.SCHEMA
     * schema, with a short description.
     */
    public static Map<String, String> getPropertyList() {
        synchronized (propertyListSync) { // Ensures initialization ended before accessing the list
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
