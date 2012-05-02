/**
 * EasySOA Proxy
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

package org.easysoa.properties;

import java.net.URL;
import java.util.Properties;
import org.apache.log4j.Logger;

/**
 * Property manager. Works with a property file named httpDiscoveryProxy.properties
 * @author jguillemotte
 *
 */
public abstract class PropertyManager {
	
    // TODO : Make this property file name configurable
	//public static final String PROPERTY_FILE_NAME = "httpDiscoveryProxy.properties";
	
	/**
	 * Logger
	 */
	private static Logger logger = Logger.getLogger(PropertyManager.class.getName());

	// Property manager
	private static PropertyManager propertyManager; 
	
	// Properties
	private static Properties properties = null;

	// Properties file name
	private static String propertiesFileName;
	
	/**
	 * 
	 * @param propsName The property file name
	 * @throws Exception If the property file cannot be loaded
	 */
	protected PropertyManager(String propsFileName) throws Exception {
	    propertiesFileName = propsFileName;
		properties = load(propsFileName);
		propertyManager = this;
	}
	
	/**
	 * Returns the properties file name
	 * @return The propeties file name
	 */
	public String getPropertiesFileName() {
	    return propertiesFileName;
	}
	
   /**
    * Load a properties file from the classpath
    * @param propsName
    * @return Properties
    * @throws Exception If the property file cannot be loaded
    */
   protected Properties load(String propsFileName) throws Exception {
       Properties props = new Properties();
       // FIXME : there is a problem here with the property file load when PropertyManager is called from a Nuxeo embedded FraSCAti 
       URL url = PropertyManager.class.getClassLoader().getResource(propsFileName);
       logger.debug("Property file url : " + url.toString());
       props.load(url.openStream());
       return props;
   }
   
   /**
    * Get the current instance of property manager
    * @return The current instance of property manager
    * @throws Exception If the property manager is not properly initialized
    */
   public static PropertyManager getPropertyManager() throws Exception {
       if(propertyManager == null){
           throw new Exception("Property manager is not properly initialized !");
       }
       return propertyManager;
   }
   
   /**
    * Returns a property value
    * @param propertyName The property name
    * @param defaultValue The default value
    * @return The value corresponding to the property, the default value if a problem occurs
    */
   public /*static*/ String getProperty(String propertyName, String defaultValue) {
	   String value;
	   try{
		   /*if (properties == null){
			   new PropertyManager(PROPERTY_FILE_NAME);
		   }*/
		   value = properties.getProperty(propertyName);
	   }
	   catch(Exception ex){
		   value = defaultValue;
		   logger.error("An error occurs during the load of property file : " + ex.getMessage());
		   logger.error("Default value is returned !");
	   }
	   return value;
   }

   /**
    * Returns a property value
    * @param propertyName The property name
    * @return The value corresponding to the property, null if a problem occurs
    */
   public /*static*/ String getProperty(String propertyName) {
	   String value = null;
	   try{
		   /*if (properties == null){
			   new PropertyManager(PROPERTY_FILE_NAME);
		   }*/
		   value = properties.getProperty(propertyName);		   
	   }
	   catch(Exception ex){
		   logger.error("An error occurs during the load of properties : " + ex.getMessage());
		   logger.error("null value is returned !");
	   }
	   return value;
   }
	
}
