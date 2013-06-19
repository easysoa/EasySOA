/**
 *
 */
package org.easysoa.proxy.management;

import org.easysoa.proxy.core.api.configuration.EasySOAGeneratedAppConfiguration;

/**
 * Container for information about generated EasySOA app's
 *
 * @author jguillemotte
 * @obsolete (should be) merged in EasySOAGeneratedAppConfiguration (& ProxyConfidutation)
 *
 */
public class EasySOAGeneratedAppInfo {

    // App ID
    private String easySOAGeneratedAppId;

    // App configuration
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
