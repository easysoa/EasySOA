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
	
	/*
	 * 
	 */
	List<ListenedService> listenedservices;
	
	List<LaunchedService> launchedservices;
	

	/**
	 *	@return  a new subscription 
	 */
	
	public Subscription(){
		this.listenedservices = new ArrayList<ListenedService>();
		this.launchedservices = new ArrayList<LaunchedService>();
	}


	public List<ListenedService> getListenedservices() {
		return listenedservices;
	}


	public void setListenedservices(List<ListenedService> listenedservices) {
		this.listenedservices = listenedservices;
	}


	public List<LaunchedService> getLaunchedservices() {
		return launchedservices;
	}


	public void setLaunchedservices(List<LaunchedService> launchedservices) {
		this.launchedservices = launchedservices;
	}
	
	public List<String> getListenedServicesUrls(){
		List<String> result = new ArrayList<String>();
		for(ListenedService lserv: this.listenedservices){
			result.add(lserv.getUrl());
		}
		return result;
	}
	public List<String> getLaunchedServicesUrls(){
		List<String> result = new ArrayList<String>();
		for(LaunchedService lserv: this.launchedservices){
			result.add(lserv.getUrl());
		}
		return result;
	}
	
	
	
}
