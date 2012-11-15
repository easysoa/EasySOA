/**
 * 
 */
package org.easysoa.proxy.registry;

import java.util.Map;

import org.easysoa.proxy.core.api.configuration.EasySOAGeneratedAppConfiguration;
import org.easysoa.proxy.management.EasySOAGeneratedAppInfo;

/**
 * @author jguillemotte
 *
 */
public class InstanciatingEasySOAGeneratedAppsRegistry {
    
    // store the proxy ID in the registry
    // List, map ...
    private Map<String, EasySOAGeneratedAppConfiguration> appRegistry;
    
    /**
     * 
     * @param appID Application ID
     */
    // TODO : how to pass the template
    // - Java Template object
    // - File URL
    public EasySOAGeneratedAppInfo get(String appID, Template template){

        // TODO : complete this method
        
        EasySOAGeneratedAppInfo appInfo = null;
        // Check if proxyID exist in the registry
        // If true, simply return it
        if(appRegistry.containsKey(appID)){
            appInfo = new EasySOAGeneratedAppInfo(appID);
            appInfo.setConfiguration(appRegistry.get(appID));
        } 
        // Otherwise creates a new one
        else {
            // call method to generate the app
            // Passing it the app id and the template required to generate the composite
            
        }
        return appInfo;
    }
    
}
