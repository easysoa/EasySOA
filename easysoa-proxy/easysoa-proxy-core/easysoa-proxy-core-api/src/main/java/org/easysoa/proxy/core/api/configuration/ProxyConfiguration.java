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

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author jguillemotte
 *
 */
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class ProxyConfiguration extends EasySOAGeneratedAppConfiguration {

    // Parameters
    @XmlElement(name = "parameters")
    private List<ConfigurationParameter> parameters;

    /**
     * Constructor
     */
    public ProxyConfiguration(){
        parameters = new ArrayList<ConfigurationParameter>();
    }
    
    /**
     * Set the parameters
     * @param parameters
     */
    @Override
    public void setParameters(List<ConfigurationParameter> parameters){
        if(parameters != null){
            this.parameters = parameters;
        } else {
            this.parameters = new ArrayList<ConfigurationParameter>();
        }
    }
    
    /**
     * 
     * @param paramName
     * @param paramValue
     */
    @Override
    public void addParameter(String paramName, String paramValue){
        parameters.add(new ConfigurationParameter(paramName, paramValue));
    }
    
    /**
     * 
     * @param paramName
     * @return
     */
    @Override
    public String getParameter(String paramName){
        String paramValue = "";
        if(paramName != null){
            for(ConfigurationParameter param : parameters){
                if(paramName.equals(param.getKey())){
                    paramValue = param.getValue();
                    break;
                }
            }
        } else {
            throw new IllegalArgumentException("paramName must not be null");
        }
        return paramValue;
    }
    
    /**
     * 
     * @param paramName
     * @return
     */
    @Override
    public boolean containsParameterName(String paramName){
        if(paramName != null){
            for(ConfigurationParameter param : parameters){
                if(paramName.equals(param.getKey())){
                    return true;
                }
            }
            return false;
        } else {
            throw new IllegalArgumentException("paramName must not be null");
        }
    }

    /**
     * 
     */
    @Override
    public List<ConfigurationParameter> getParameters() {
        return this.parameters;
    }   
    
}
