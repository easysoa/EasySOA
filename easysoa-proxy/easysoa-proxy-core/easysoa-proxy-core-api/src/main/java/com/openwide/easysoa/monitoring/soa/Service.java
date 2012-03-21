/**
 * EasySOA Proxy
 * Copyright 2011 Open Wide
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * 
 * Contact : easysoa-dev@googlegroups.com
 */

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
	private String participants;
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
		this.participants = "";
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
	 * @return the participants
	 */
	public String getParticipants() {
		return participants;
	}
	/**
	 * @param participants the participants to set
	 */
	public void setParticipants(String participants) {
		this.participants = participants;
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
