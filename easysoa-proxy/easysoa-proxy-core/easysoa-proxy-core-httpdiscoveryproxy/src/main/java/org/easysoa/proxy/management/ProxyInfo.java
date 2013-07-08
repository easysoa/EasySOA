/**
 *
 */
package org.easysoa.proxy.management;

import org.easysoa.proxy.core.api.configuration.ProxyConfiguration;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Contains informations about created proxy
 *
 * @author jguillemotte
 * @obsolete
 *
 */

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class ProxyInfo {

    // TODO : add information about proxy instance
    @XmlElement(name = "proxyName")
    private String proxyName;
    @XmlElement(name = "proxyID")
    private String proxyID;
    @XmlElement(name = "configuration")
    private ProxyConfiguration configuration;

    // contains utils informations about the proxy
    // eg : port, url ... for clients configuration
    // => can returns the ProxyConfiguration completed with default values

    public String getProxyName() {
        return proxyName;
    }

    public void setProxyName(String proxyName) {
        this.proxyName = proxyName;
    }

    public String getProxyID() {
        return proxyID;
    }

    public void setProxyID(String proxyID) {
        this.proxyID = proxyID;
    }

    public ProxyConfiguration getConfiguration() {
        return configuration;
    }

    public void setConfiguration(ProxyConfiguration configuration) {
        this.configuration = configuration;
    }

}
