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
