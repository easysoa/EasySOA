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

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Properties;
import org.apache.log4j.Logger;

/**
 * Property manager. Works with a property file named httpDiscoveryProxy.properties
 * @author jguillemotte
 *
 */
public abstract class PropertyManager {
	
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
	    logger.debug("Creates a new PropertyManager ....");
	    propertiesFileName = propsFileName;
		properties = load(propsFileName);
		propertyManager = this;
	}
	
	/**
	 * 
	 * @param propsFileName
	 * @param propsFileStream
	 * @throws Exception
	 */
	protected PropertyManager(String propsFileName, InputStream propsFileStream) throws Exception {
        logger.debug("Creates a new PropertyManager ....");
        propertiesFileName = propsFileName;
        properties = load(propsFileStream);
        propertyManager = this;
	}
	
	/**
	 * Returns the properties file name
	 * @return The properties file name
	 */
	public String getPropertiesFileName() {
	    return propertiesFileName;
	}
	
	/**
	 * Load a property file form an input stream
	 * @param propsFileStream The input stream containing the property file
	 * @return 
	 * @throws IOException If a problem occurs
	 */
	protected Properties load(InputStream propsFileStream) throws Exception{
	    logger.debug("Loading property file form input stream : '" + propsFileStream +  "'");   
	    Properties props = new Properties();
	    if(propsFileStream != null){
	        props.load(propsFileStream);
	    } else {
	        throw new Exception("Unable to load the property file from a null input stream");
	    }
	    return props;	    
	}
	
   /**
    * Load a properties file from the classpath
    * @param propsName
    * @return Properties
    * @throws Exception If the property file cannot be loaded
    */
   protected Properties load(String propsFileName) throws Exception {
       logger.debug("Loading property file  '" + propsFileName +  "'");
       Properties props = new Properties();
       URL url = PropertyManager.class.getClassLoader().getResource(propsFileName);
       if(url != null){
           logger.debug("Property file url : " + url.toString());
           props.load(url.openStream());
       } else {
           // Resource not found, trying another method
           InputStream propFileInputStream = this.getClass().getResourceAsStream(propsFileName);
           if(propFileInputStream != null){
               props.load(propFileInputStream);
           } else {
               throw new Exception("Unable to load the property file named " + propsFileName);
           }
       }
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
   public String getProperty(String propertyName, String defaultValue) {
	   String value;
	   try{
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
   public String getProperty(String propertyName) {
	   String value = null;
	   try{
		   value = properties.getProperty(propertyName);		   
	   }
	   catch(Exception ex){
		   logger.error("An error occurs during the load of properties : " + ex.getMessage());
		   logger.error("null value is returned !");
	   }
	   return value;
   }
	
}
