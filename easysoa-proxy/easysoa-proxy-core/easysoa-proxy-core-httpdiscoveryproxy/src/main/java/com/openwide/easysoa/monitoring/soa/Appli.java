package com.openwide.easysoa.monitoring.soa;

public class Appli extends Node {

	/*
	 * { "description":
	 * "Notification concerning an application implementation.", "parameters": {
	 * "rootServicesUrl": "(mandatory) Services root.", "uiUrl":
	 * "Application GUI entry point.", "title": "The name of the document.",
	 * "technology": "Services implementation technology.", "standard":
	 * "Protocol standard if applicable.", "sourcesUrl": "Source code access.",
	 * "description": "A short description.", "server": "IP of the server." } }
	 */

	/**
	 * Application GUI entry point.
	 */
	private String uiUrl;
	
	/**
	 * Services implementation technology
	 */
	private String technology;
	
	/**
	 * Protocol standard if applicable
	 */
	private String standard;
	
	/**
	 * Source code access
	 */
	private String sourcesUrl;
	
	/**
	 * IP of the server.
	 */
	private String server;

	/**
	 * 
	 * @param rootServicesUrl
	 * @throws IllegalArgumentException
	 */
	public Appli(String appliName, String url) throws IllegalArgumentException {
		super(url);
		if(appliName == null || "".equals(appliName)){
			throw new IllegalArgumentException("appliname must not be null or empty");
		}
		this.server = "";
		this.sourcesUrl = "";
		this.standard = "";
		this.technology = "";
		this.uiUrl = "";
	}
	
	public String getUiUrl() {
		return uiUrl;
	}

	public void setUiUrl(String uiUrl) {
		this.uiUrl = uiUrl;
	}

	public String getTechnology() {
		return technology;
	}

	public void setTechnology(String technology) {
		this.technology = technology;
	}

	public String getStandard() {
		return standard;
	}

	public void setStandard(String standard) {
		this.standard = standard;
	}

	public String getSourcesUrl() {
		return sourcesUrl;
	}

	public void setSourcesUrl(String sourcesUrl) {
		this.sourcesUrl = sourcesUrl;
	}

	public String getServer() {
		return server;
	}

	public void setServer(String server) {
		this.server = server;
	}
}
