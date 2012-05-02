/**
 * 
 */
package com.openwide.easysoa.proxy.properties;

import org.easysoa.properties.PropertyManager;

/**
 * @author jguillemotte
 *
 */
public class ProxyPropertyManager extends PropertyManager {

    public static final String PROPERTY_FILE_NAME = "httpDiscoveryProxy.properties";
    
    public ProxyPropertyManager() throws Exception {
        super(PROPERTY_FILE_NAME);
    }
    
}
