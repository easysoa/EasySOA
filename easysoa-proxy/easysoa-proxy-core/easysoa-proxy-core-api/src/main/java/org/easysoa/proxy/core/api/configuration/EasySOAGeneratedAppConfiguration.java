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

import java.util.List;

/**
 * @author jguillemotte
 *
 */
public abstract class EasySOAGeneratedAppConfiguration {

    // Parameter names to be used in ProxyConfiguration
    public static final String USER_PARAM_NAME = "user";
    public static final String PROJECTID_PARAM_NAME = "projectId";
    public static final String ENVIRONMENT_PARAM_NAME = "environment";
    public static final String COMPONENTID_PARAM_NAME = "componentIds";
    public static final String PROXY_PORT_PARAM_NAME = "proxyPort";
    public static final String PROXY_PATH_PARAM_NAME = "proxyPath";
    public static final String PROXY_HOST_PARAM_NAME = "proxyHost";
    public static final String PROXY_CREATION_STRATEGY_PARAM_NAME = "proxyCreationStrategy";

    /**
     * Set the parameters
     * @param parameters
     */
    public abstract void setParameters(List<ConfigurationParameter> parameters);
    
    /**
     * 
     * @return
     */
    public abstract List<ConfigurationParameter> getParameters();
    
    /**
     * 
     * @param paramName
     * @param paramValue
     */
    public abstract void addParameter(String paramName, String paramValue);
    
    /**
     * 
     * @param paramName
     * @return
     */
    public abstract String getParameter(String paramName);
    
    /**
     * 
     * @param paramName
     * @return
     */
    public abstract boolean containsParameterName(String paramName);    
    
}
