/**
 * 
 */
package org.easysoa.proxy.core.api.configuration;

/**
 * Key/value container for configuration parameters
 * 
 * @author jguillemotte
 *
 */
public class ConfigurationParameter {

    // Key
    private String key = "";    
    // Value
    private String value = "";    

    /**
     * Default constructor
     */
    public ConfigurationParameter() {
    }

    /**
     * 
     * @param paramName
     * @param paramValue
     */
    public ConfigurationParameter(String paramName, String paramValue) {
        this.key = paramName;
        this.value = paramValue;
    }    
    
    /**
     * 
     * @return
     */
    public String getKey() {
        return key;
    }

    /**
     * 
     * @param key
     */
    public void setKey(String key) {
        this.key = key;
    }

    /**
     * 
     * @return
     */
    public String getValue() {
        return value;
    }

    /**
     * 
     * @param value
     */
    public void setValue(String value) {
        this.value = value;
    }
   
}
