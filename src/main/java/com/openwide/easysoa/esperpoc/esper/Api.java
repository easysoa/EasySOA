package com.openwide.easysoa.esperpoc.esper;

public class Api {

    private String title;
    private String application;
    private String description;
    private String parentUrl;
    private String sourceUrl;
    private String url;
    /*
	"title": "The name of the document.",
    "application": "The related business application.",
    "description": "A short description.",
    "parentUrl": "(mandatory) The parent URL, which is either another service API, or the service root.",
    "sourceUrl": "The web page where the service has been found (useful for REST only).",
    "url": "(mandatory) Service API url (WSDL address, parent path...)."
    */
    
    public Api(String url, String parentUrl) throws IllegalArgumentException {
		if(url == null || parentUrl == null || "".equals(url) || "".equals(parentUrl)){
			throw new IllegalArgumentException("url and parentUrl must not be null or empty");
		}
    	this.url = url;
    	this.parentUrl = parentUrl;
    	this.title = "";
    	this.description = "";
    	this.application = "";
    	this.sourceUrl = "";
    }
    
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getApplication() {
		return application;
	}
	public void setApplication(String application) {
		this.application = application;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
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
	public String getUrl() {
		return url;
	}
}
