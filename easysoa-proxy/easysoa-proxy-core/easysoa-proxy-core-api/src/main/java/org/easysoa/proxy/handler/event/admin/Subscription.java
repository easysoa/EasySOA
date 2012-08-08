package org.easysoa.proxy.handler.event.admin;


import java.util.*;

import javax.xml.bind.annotation.XmlRootElement;

import com.espertech.esper.collection.Pair;

/**
 * @author fntangke
 * 
 */


@XmlRootElement(name = "subscription")
public class Subscription {
	
    // Subscriptions [Subscription{ ListenedService{ id, url }, LaunchServices[LaunchService{ id, url }]}]
    //Map<List<ListenedService> ,List<LaunchedServices>> content;

    //List<ListenedService> listenedservices;
    private ListenedService listenedService;
    private List<LaunchedService> launchedservices;


    /**
     *	@return  a new subscription 
     */

    public Subscription(){
            this.listenedService = new ListenedService();
            this.launchedservices = new ArrayList<LaunchedService>();
    }

    /**
     * @return the launchedservices
     */
    public List<LaunchedService> getLaunchedservices() {
        return launchedservices;
    }

    /**
     * @param launchedservices the launchedservices to set
     */
    public void setLaunchedservices(List<LaunchedService> launchedservices) {
        this.setLaunchedservices(launchedservices);
    }

    public List<String> getLaunchedServiceUrl(){
        List<String> listLaunchedService = new ArrayList<String>();
        for(LaunchedService launchedService: launchedservices){
            listLaunchedService.add(launchedService.getUrl());
        }
        return listLaunchedService;
    }

    /**
     * @return the listenedService
     */
    public ListenedService getListenedService() {
        return listenedService;
    }

    /**
     * @param listenedService the listenedService to set
     */
    public void setListenedService(ListenedService listenedService) {
        this.listenedService = listenedService;
    }

  
}
