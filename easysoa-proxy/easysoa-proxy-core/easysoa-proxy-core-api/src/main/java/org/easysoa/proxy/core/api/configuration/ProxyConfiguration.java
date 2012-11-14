/**
 * 
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
    public void setParameters(List<ConfigurationParameter> parameters){
        if(parameters != null){
            this.parameters = parameters;
        } else {
            parameters = new ArrayList<ConfigurationParameter>();
        }
    }
    
    /**
     * 
     * @param paramName
     * @param paramValue
     */
    public void addParameter(String paramName, String paramValue){
        parameters.add(new ConfigurationParameter(paramName, paramValue));
    }
    
    /**
     * 
     * @param paramName
     * @return
     */
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
    public List<ConfigurationParameter> getParameters() {
        return this.parameters;
    }   
    
}
