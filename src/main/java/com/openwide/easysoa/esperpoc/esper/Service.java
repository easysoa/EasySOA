package com.openwide.easysoa.esperpoc.esper;

public class Service {

	/*
	{
	  "description": "Service-level notification.",
	  "parameters": {
	    "contentTypeOut": "HTTP content type of the result body",
	    "relatedUsers": "Users that have been using the service",
	    "title": "The name of the document.",
	    "contentTypeIn": "HTTP content type of the request body",
	    "httpMethod": "POST, GET...",
	    "description": "A short description.",
	    "parentUrl": "(mandatory) Service API URL (WSDL address, parent path...)",
	    "callcount": "Times the service has been called since last notification",
	    "url": "(mandatory) Service URL."
	  }
	}
	*/
	
	private String contentTypeOut;
	private String relatedUsers;
	private String title;
	private String contentTypeIn;
	private String httpMethod;
	private String description;
	private String parentUrl;
	private int callCount;
	private String url;
	
	/**
	 * 
	 * @param url
	 * @param parentUrl
	 */
	public Service(String url, String parentUrl) throws IllegalArgumentException {
		if(url == null || parentUrl == null || "".equals(url) || "".equals(parentUrl)){
			throw new IllegalArgumentException("url and parentUrl parameters must not be null");
		}
		this.url = url;
		this.parentUrl = parentUrl;
		this.callCount = 0;
		this.contentTypeIn = "";
		this.contentTypeOut = "";
		this.title = "";
		this.httpMethod = "";
		this.description = "";
		this.relatedUsers = "";
	}
	
	/**
	 * @return the contentTypeOut
	 */
	public String getContentTypeOut() {
		return contentTypeOut;
	}
	/**
	 * @param contentTypeOut the contentTypeOut to set
	 */
	public void setContentTypeOut(String contentTypeOut) {
		this.contentTypeOut = contentTypeOut;
	}
	/**
	 * @return the relatedUsers
	 */
	public String getRelatedUsers() {
		return relatedUsers;
	}
	/**
	 * @param relatedUsers the relatedUsers to set
	 */
	public void setRelatedUsers(String relatedUsers) {
		this.relatedUsers = relatedUsers;
	}
	/**
	 * @return the title
	 */
	public String getTitle() {
		return title;
	}
	/**
	 * @param title the title to set
	 */
	public void setTitle(String title) {
		this.title = title;
	}
	/**
	 * @return the contentTypeIn
	 */
	public String getContentTypeIn() {
		return contentTypeIn;
	}
	/**
	 * @param contentTypeIn the contentTypeIn to set
	 */
	public void setContentTypeIn(String contentTypeIn) {
		this.contentTypeIn = contentTypeIn;
	}
	/**
	 * @return the httpMethod
	 */
	public String getHttpMethod() {
		return httpMethod;
	}
	/**
	 * @param httpMethod the httpMethod to set
	 */
	public void setHttpMethod(String httpMethod) {
		this.httpMethod = httpMethod;
	}
	/**
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}
	/**
	 * @param description the description to set
	 */
	public void setDescription(String description) {
		this.description = description;
	}
	/**
	 * @return the callCount
	 */
	public int getCallCount() {
		return callCount;
	}
	/**
	 * @param callCount the callCount to set
	 */
	public void setCallCount(int callCount) {
		this.callCount = callCount;
	}
	/**
	 * @return the parentUrl
	 */
	public String getParentUrl() {
		return parentUrl;
	}
	/**
	 * @return the url
	 */
	public String getUrl() {
		return url;
	}
	
}
