package com.openwide.easysoa.esperpoc.esper;

public class Appli {

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
	 * A short description.
	 */
	private String description;
	
	/**
	 * (mandatory) Services root.
	 */
	private String rootServicesUrl;
	
	/**
	 * Application GUI entry point.
	 */
	private String uiUrl;
	
	/**
	 * The name of the document.
	 */
	private String title;

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
	public Appli(String appliName, String rootServicesUrl) throws IllegalArgumentException {
		if(rootServicesUrl == null || appliName == null || "".equals(appliName) || "".equals(rootServicesUrl)){
			throw new IllegalArgumentException("appliname and rootServiceUrl parameters must not be null");
		}
		this.rootServicesUrl = rootServicesUrl;
		this.title = appliName;
		this.server = "";
		this.sourcesUrl = "";
		this.standard = "";
		this.description = "";
		this.technology = "";
		this.uiUrl = "";
	}
	
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getRootServicesUrl() {
		return rootServicesUrl;
	}

	public String getUiUrl() {
		return uiUrl;
	}

	public void setUiUrl(String uiUrl) {
		this.uiUrl = uiUrl;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
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
