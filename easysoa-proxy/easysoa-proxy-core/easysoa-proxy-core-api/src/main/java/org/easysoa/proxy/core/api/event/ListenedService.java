package org.easysoa.proxy.core.api.event;

import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author fntangke
 *
 */
@XmlRootElement(name = "listenedService")
public class ListenedService {

    private String url;

    public ListenedService() {
        // TODO Auto-generated constructor stub
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
