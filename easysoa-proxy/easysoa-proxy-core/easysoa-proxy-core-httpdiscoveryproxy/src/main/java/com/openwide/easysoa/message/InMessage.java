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

package com.openwide.easysoa.message;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.CharBuffer;
import java.util.Enumeration;

import javax.servlet.http.HttpServletRequest;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * HTTP incoming message
 * @author jguillemotte
 * 
 */
@XmlRootElement
public class InMessage implements Message {

	/**
	 * Protocol (http, htpps, ...)
	 */
	private String protocol;
	/**
	 * Method (get, post ...)
	 */
	private String method;
	/**
	 * Serveur (www.easysoa.org, ...) or ip address
	 */
	private String server;
	/**
	 * Port 
	 */
	private int port;
	/**
	 * Path : only the part after the port number
	 */
	private String path;
	/**
	 * Complete url including protocol, server, port, path
	 */
	private String completeUrl;
	/**
	 * Protocol version
	 */
	private String protocolVersion;
	/**
	 * Message headers
	 */
	private Headers headers;
	/**
	 * QueryString : url parameters after the first ?
	 */
	private QueryString queryString;
	/**
	 * Message body data
	 */
	private PostData postData;
	/**
	 * Optional comment
	 */
	private String comment;
	/**
	 * Message body or entity 
	 */
	private MessageContent messageContent;
	
	
	// private Long headersSize;
	// private Long bodySize;
	private CustomFields customFields = new CustomFields();

	public MessageContent getMessageContent() {
		return messageContent;
	}

	public void setMessageContent(MessageContent messageContent) {
		this.messageContent = messageContent;
	}

	/**
	 * Default constructor
	 */
	public InMessage(){}
	
	// TODO : replace path by the complete URL
	public InMessage(String method, String path) {
		this.method = method;
		this.path = path;
	}	
	
	/**
	 * Build a InMessage from an HttpServletrequest
	 * @param request The HttpservletRequest
	 */
	public InMessage(HttpServletRequest request){
		this.method = request.getMethod();
		// Set the headers
		this.headers = new Headers();
		Enumeration<String> headerNameEnum = request.getHeaderNames();
		while(headerNameEnum.hasMoreElements()){
			String headerName = headerNameEnum.nextElement();
			this.headers.addHeader(new Header(headerName, request.getHeader(headerName)));
		}
		// Set protocol, server, port, path
		this.protocol = request.getProtocol().substring(0,request.getProtocol().indexOf('/'));
		this.protocolVersion = request.getProtocol().substring(request.getProtocol().indexOf('/')+1);
		this.server = request.getServerName();
		this.port = request.getServerPort();
		this.path = request.getRequestURI();
		this.completeUrl = request.getRequestURL().toString();
		// Set url parameters
		this.queryString = new QueryString();
		Enumeration<String> parametersNameEnum = request.getParameterNames(); 
		while(parametersNameEnum.hasMoreElements()){
			String parameterName = parametersNameEnum.nextElement();
			for(String parameterValue : request.getParameterValues(parameterName)){
				this.queryString.addQueryParam(new QueryParam(parameterName, parameterValue));
			}
		}
		this.messageContent = new MessageContent();
	    StringBuffer requestBody = new StringBuffer();
	    BufferedReader requestBodyReader = null;
	    CharBuffer buffer = CharBuffer.allocate(512); 
		try {
			requestBodyReader = request.getReader();
		    while(requestBodyReader.ready()){
		    	requestBodyReader.read(buffer);
		    	requestBody.append(buffer.rewind());
		    }
			this.messageContent.setContent(requestBody.toString());
			this.messageContent.setSize(requestBody.length());
			this.messageContent.setMimeType(request.getContentType());
		} catch (IOException ex) {
			//logger.error("Error while reading request body !", ex);
		} finally {	    
			try {
				requestBodyReader.close();
			} catch (IOException ex) {
				//logger.warn("Error while closing the requestBodyReader !", ex);
			}
		}
		this.comment = "";
	}
	
	public String getProtocol() {
		return protocol;
	}

	public void setProtocol(String protocol) {
		this.protocol = protocol;
	}

	public String getServer() {
		return server;
	}

	public void setServer(String server) {
		this.server = server;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public void setMethod(String method){
		this.method = method;
	}
	
	public String getMethod() {
		return method;
	}

	public String getProtocolVersion() {
		return protocolVersion;
	}

	public void setProtocolVersion(String protocolVersion) {
		this.protocolVersion = protocolVersion;
	}

	public Headers getHeaders() {
		return headers;
	}

	public void setHeaders(Headers headers) {
		this.headers = headers;
	}

	public QueryString getQueryString() {
		return queryString;
	}

	public void setQueryString(QueryString queryString) {
		this.queryString = queryString;
	}

	public PostData getPostData() {
		return postData;
	}

	public void setPostData(PostData postData) {
		this.postData = postData;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public CustomFields getCustomFields() {
		return customFields;
	}

	public void setCustomFields(CustomFields customFields) {
		this.customFields = customFields;
	}

	public String getPath() {
		return path;
	}
	
	public void setPath(String path){
		this.path = path;
	}

	public String getCompleteUrl() {
		return completeUrl;
	}

	public void setCompleteUrl(String completeUrl) {
		this.completeUrl = completeUrl;
	}	
	
}
