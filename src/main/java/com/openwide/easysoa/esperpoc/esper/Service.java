package com.openwide.easysoa.esperpoc.esper;

public class Service {

	public String appName;
	public String serviceName;
	public String url;
	
	/**
	 * Constructor
	 * @param appName Application name
	 * @param serviceName Service name
	 * @param url Service URL
	 */
	public Service(String appName, String serviceName, String url){
		this.appName = appName;
		this.serviceName = serviceName;
		this.url = url;
	}

	/**
	 * Application name
	 * @return Application name
	 */
	public String getAppName() {
		return appName;
	}

	/**
	 * Service name
	 * @return Service name
	 */
	public String getServiceName(){
		return serviceName;
	}
	
	/**
	 * Service URL
	 * @return Service URL
	 */
	public String getUrl() {
		return url;
	}
	
}
