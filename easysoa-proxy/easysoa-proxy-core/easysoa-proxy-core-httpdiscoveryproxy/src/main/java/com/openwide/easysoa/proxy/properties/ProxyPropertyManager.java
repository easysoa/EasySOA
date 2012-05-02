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
     * 
     * @throws Exception
     */
    public ProxyPropertyManager() 
            throws Exception {
        super(PROPERTY_FILE_NAME);
    }
    
    /**
     * 
     * @param propFileName
     * @param propFileInputStream
     * @throws Exception
     */
    public ProxyPropertyManager(String propFileName, InputStream propFileInputStream) throws Exception{
        super(propFileName, propFileInputStream);
    }
    
}
 