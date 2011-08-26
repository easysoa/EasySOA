package com.openwide.easysoa.monitoring.soa;

public class Service extends Node {

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
	private String contentTypeIn;
	private String httpMethod;
	private String parentUrl;
	private String fileUrl;
	private int callCount;
	
	/**
	 * 
	 * @param url
	 * @param parentUrl
	 */
	public Service(String url) throws IllegalArgumentException {
		super(url);
		this.callCount = 0;
		this.contentTypeIn = "";
		this.contentTypeOut = "";
		this.httpMethod = "";
		this.relatedUsers = "";
		this.parentUrl = "";
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
	 * @param parentUrl the parentUrl to set
	 */
	public void setParentUrl(String parentUrl) {
		this.parentUrl = parentUrl;
	}
	/**
	 * @return the fileUrl
	 */
	public String getFileUrl() {
		return fileUrl;
	}
	/**
	 * @param fileUrl the fileUrl to set
	 */
	public void setFileUrl(String fileUrl) {
		this.fileUrl = fileUrl;
	}
	
}
