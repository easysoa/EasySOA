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
public class PropertyManager {
	
    // TODO : Make this property file name configurable ??
	public static final String PROPERTY_FILE_NAME = "httpDiscoveryProxy.properties";
	
	/**
	 * Logger
	 */
	private static Logger logger = Logger.getLogger(PropertyManager.class.getName());

	/**
	 * Properties
	 */
	private static Properties props = null;
	
	/**
	 * 
	 * @param propsName The property file name
	 * @throws Exception If the property file cannot be loaded
	 */
	private PropertyManager(String propsFileName) throws Exception {
		props = PropertyManager.load(propsFileName);
	}
	
   /**
    * Load a properties file from the classpath
    * @param propsName
    * @return Properties
    * @throws Exception If the property file cannot be loaded
    */
   private static Properties load(String propsFileName) throws Exception {
       Properties props = new Properties();
       URL url = PropertyManager.class.getClassLoader().getResource(propsFileName);
       props.load(url.openStream());
       return props;
   }

   /**
    * Returns a property value
    * @param propertyName The property name
    * @param defaultValue The default value
    * @return The value corresponding to the property, the default value if a problem occurs
    */
   public static String getProperty(String propertyName, String defaultValue) {
	   String value;
	   try{
		   if (props == null){
			   new PropertyManager(PROPERTY_FILE_NAME);
		   }
		   value = props.getProperty(propertyName);
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
   public static String getProperty(String propertyName) {
	   String value = null;
	   try{
		   if (props == null){
			   new PropertyManager(PROPERTY_FILE_NAME);
		   }
		   value = props.getProperty(propertyName);		   
	   }
	   catch(Exception ex){
		   logger.error("An error occurs during the load of properties : " + ex.getMessage());
		   logger.error("null value is returned !");
	   }
	   return value;
   }
	
}
