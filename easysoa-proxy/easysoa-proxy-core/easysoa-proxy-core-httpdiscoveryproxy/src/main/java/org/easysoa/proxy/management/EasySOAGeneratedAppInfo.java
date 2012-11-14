/**
 * 
 */
package org.easysoa.proxy.management;

import org.easysoa.proxy.core.api.configuration.EasySOAGeneratedAppConfiguration;

/**
 * @author jguillemotte
 *
 */
public class EasySOAGeneratedAppInfo {

    //
    private String easySOAGeneratedAppId;
    
    //
    private EasySOAGeneratedAppConfiguration configuration;
   
    /**
     * 
     * @param id
     */
    public EasySOAGeneratedAppInfo(String id){
        this.setEasySOAGeneratedAppId(id);
    }

    /**
     * 
     * @return
     */
    public EasySOAGeneratedAppConfiguration getConfiguration() {
        return configuration;
    }

    /**
     * 
     * @param configuration
     */
    public void setConfiguration(EasySOAGeneratedAppConfiguration configuration) {
        this.configuration = configuration;
    }

    /**
     * 
     * @return
     */
    public String getEasySOAGeneratedAppId() {
        return easySOAGeneratedAppId;
    }

    /**
     * 
     * @param easySOAGeneratedAppId
     */
    private void setEasySOAGeneratedAppId(String easySOAGeneratedAppId) {
        this.easySOAGeneratedAppId = easySOAGeneratedAppId;
    }
    
    
    
}
