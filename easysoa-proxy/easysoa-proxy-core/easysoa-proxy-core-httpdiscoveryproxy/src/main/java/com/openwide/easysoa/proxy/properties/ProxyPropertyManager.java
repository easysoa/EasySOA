/**
 * 
 */
package com.openwide.easysoa.proxy.properties;

import java.io.InputStream;

import org.easysoa.properties.PropertyManager;

/**
 * @author jguillemotte
 *
 */
public class ProxyPropertyManager extends PropertyManager {

    // Default proxy property file name
    public static final String PROPERTY_FILE_NAME = "httpDiscoveryProxy.properties";
    
    /**
     * Create a proxy property manager using the default property file
     * @throws Exception If a problem occurs
     */
    public ProxyPropertyManager() throws Exception {
        super(PROPERTY_FILE_NAME);
    }
    
    /**
     * Create a proxy property manager using a custom property file
     * @param propertyFileName The property file name
     * @throws Exception If a problem occurs
     */
    public ProxyPropertyManager(String propertyFileName) throws Exception {
        super(propertyFileName);
    }
    
    /**
     * Create a proxy property manager using a custom property file
     * @param propFileName The property file name
     * @param propFileInputStream The property file input stream
     * @throws Exception If a problem occurs
     */
    public ProxyPropertyManager(String propFileName, InputStream propFileInputStream) throws Exception{
        super(propFileName, propFileInputStream);
    }
    
}
 