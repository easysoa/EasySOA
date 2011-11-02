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
    public static final String FEATURE_SCHEMA = "feature";
    public static final String FEATURE_SCHEMA_PREFIX = "feat:";
    
    public static final String DEFAULT_ENVIRONMENT = "Production";
    public static final String DEFAULT_APPLIIMPL_TITLE = "Default application";
    public static final String DEFAULT_APPLIIMPL_URL = "(Unknown)";
    
    // Application properties
    public static final String PROP_URL = "url";
    public static final String PROP_UIURL = "uiUrl";
    public static final String PROP_SOURCESURL = "sourcesUrl";
    public static final String PROP_SERVER = "server";
    public static final String PROP_PROVIDER = "provider";
    public static final String PROP_ENVIRONMENT = "environment";
    public static final String PROP_TECHNOLOGY = "technology";
    public static final String PROP_STANDARD = "standard";
    
    // Feature-specific properties
    public static final String PROP_DOMAIN = "domain";
    public static final String PROP_LIFECYCLESTATUS = "lifeCycleStatus";
    public static final String PROP_DESIGNDOCUMENTNAME = "designDocumentName";
    public static final String PROP_DESIGNDOCUMENTSOURCE = "designDocumentSource";
    
    public static final String PROP_SERVERENTRY = "serverEntry"; // Internal
    
    private static Map<String, String> propertyList = null;
    private static Object propertyListSync = new Object();
    private static Map<String, String> featurePropertyList = null;
    private static Object featurePropertyListSync = new Object();
    
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
    
    public static Map<String, String> getFeaturePropertyList() {
        synchronized (featurePropertyListSync) {
            featurePropertyList = new HashMap<String, String>(); 
            featurePropertyList.put(PROP_DOMAIN, "Feature's business domain.");
            featurePropertyList.put(PROP_LIFECYCLESTATUS, "Feature's lifecycle status.");
            featurePropertyList.put(PROP_DESIGNDOCUMENTNAME, "The name of the attached design document.");
            featurePropertyList.put(PROP_DESIGNDOCUMENTSOURCE, "The origin of the attached design document.");
        }
        return featurePropertyList;
    }
}