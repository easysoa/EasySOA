/**
 * EasySOA Proxy
 * Copyright 2011-2013 Open Wide
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

package org.easysoa.proxy.core.api.management;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import org.easysoa.proxy.core.api.configuration.EasySOAGeneratedAppConfiguration;

/**
 *
 * @author jguillemotte
 */
@XmlRootElement(name="managementServiceresult")
@XmlAccessorType(XmlAccessType.PROPERTY)
public class ManagementServiceResult {

    public final static String RESULT_OK = "OK";
    public final static String RESULT_KO = "KO";

    private String status;
    private String message;
    private EasySOAGeneratedAppConfiguration configuration;

    /**
     * Default constructor initialized with the following values :
     * - status = ""
     * - message = ""
     * - configuration = null
     */
    public ManagementServiceResult(){
        this.status = "";
        this.message = "";
        this.configuration = null;
    }

    /**
     *
     * @param status Result status
     * @param configuration Configuration
     */
    public ManagementServiceResult(String status, EasySOAGeneratedAppConfiguration configuration){
        this.status = status;
        this.message = "";
        this.configuration = configuration;
    }

    /**
     * @return the status
     */
    public String getStatus() {
        return this.status;
    }

    /**
     * @param status the status to set
     */
    public void setStatus(String status) {
        this.status = status;
    }

    /**
     * @return the message
     */
    public String getMessage() {
        return this.message;
    }

    /**
     * @param message the message to set
     */
    public void setMessage(String message) {
        this.message = message;
    }

    /**
     * @return the configuration
     */
    public EasySOAGeneratedAppConfiguration getConfiguration() {
        return this.configuration;
    }

    /**
     * @param configuration the configuration to set
     */
    public void setConfiguration(EasySOAGeneratedAppConfiguration configuration) {
        this.configuration = configuration;
    }

}
