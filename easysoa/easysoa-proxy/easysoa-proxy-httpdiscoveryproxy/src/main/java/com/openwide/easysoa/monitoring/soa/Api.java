package com.openwide.easysoa.monitoring.soa;

public class Api extends Node {

    private String application;
    private String parentUrl;
    private String sourceUrl;
    /*
	"title": "The name of the document.",
    "application": "The related business application.",
    "description": "A short description.",
    "parentUrl": "(mandatory) The parent URL, which is either another service API, or the service root.",
    "sourceUrl": "The web page where the service has been found (useful for REST only).",
    "url": "(mandatory) Service API url (WSDL address, parent path...)."
    */
    
    public Api(String url, String parentUrl) throws IllegalArgumentException {
    	super(url);
		if(parentUrl == null || "".equals(parentUrl)){
			throw new IllegalArgumentException("parentUrl must not be null or empty");
		}
    	this.parentUrl = parentUrl;
    	this.application = "";
    	this.sourceUrl = "";
    }
    
	public String getApplication() {
		return application;
	}
	public void setApplication(String application) {
		this.application = application;
	}
	public String getSourceUrl() {
		return sourceUrl;
	}
	public void setSourceUrl(String sourceUrl) {
		this.sourceUrl = sourceUrl;
	}
	public String getParentUrl() {
		return parentUrl;
	}
}
