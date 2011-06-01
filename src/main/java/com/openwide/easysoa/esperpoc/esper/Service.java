package com.openwide.easysoa.esperpoc.esper;

public class Service {

	private String appName;
	private String serviceName;
	private String url;
	private String type;
	
	/**
	 * Constructor
	 * @param appName Application name
	 * @param serviceName Service name
	 * @param url Service URL
	 */
	public Service(String appName, String serviceName, String url, String type){
		this.appName = appName;
		this.serviceName = serviceName;
		this.url = url;
		this.type = type;
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
	
	/**
	 * Service type : WSDl, REST ...
	 * @return
	 */
	public String getType(){
		return type;
	}
}
