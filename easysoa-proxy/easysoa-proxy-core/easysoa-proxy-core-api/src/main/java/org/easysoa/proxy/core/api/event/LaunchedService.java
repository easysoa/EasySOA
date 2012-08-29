package org.easysoa.proxy.core.api.event;

import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author fntangke
 *
 */
@XmlRootElement(name = "launchedService")
public class LaunchedService {

    //private int id;
    private String url;

    public LaunchedService() {
        // TODO Auto-generated constructor stub
    }
    public LaunchedService(String url){
    	this.url = url;
    }

    /**
     * @return the url
     */
    public String getUrl() {
        return url;
    }

    /**
     * @param url the url to set
     */
    public void setUrl(String url) {
        this.url = url;
    }
}
