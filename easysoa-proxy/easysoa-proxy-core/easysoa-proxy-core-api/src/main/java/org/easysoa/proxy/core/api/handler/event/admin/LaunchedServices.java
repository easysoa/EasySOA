package org.easysoa.proxy.core.api.handler.event.admin;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author fntangke
 */
@XmlRootElement
public class LaunchedServices {

    private List<LaunchedService> launchedServices;

    public LaunchedServices() {
        this.launchedServices = new ArrayList<LaunchedService>();
    }

    public LaunchedServices(List<LaunchedService> lservices) {
        this.launchedServices = lservices;
    }

    public List<LaunchedService> getLaunchedServices() {
        return launchedServices;
    }

    public void setLaunchedServices(List<LaunchedService> launchedServices) {
        this.launchedServices = launchedServices;
    }
}
