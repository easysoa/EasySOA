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
import java.io.InputStreamReader;
import java.nio.CharBuffer;
import java.util.Enumeration;
import javax.xml.bind.annotation.XmlRootElement;
import org.apache.log4j.Logger;
import org.easysoa.servlet.http.HttpMessageRequestWrapper;

/**
 * HTTP incoming message
 * @author jguillemotte
 * 
 */
@XmlRootElement
@SuppressWarnings("unchecked")
public class InMessage implements Message {

	// Logger
	private static Logger logger = Logger.getLogger(InMessage.class.getName());
	
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
	 * Path : only the part after the port number and before the query params
	 */
	// TODO : maybe a good idea to store path as an ArrayList of path segments
	private String path;
	/**
	 * Complete url including protocol, server, port, path
	 */
	//private String completeUrl;
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
	//private CustomFields customFields;
	
	private long requestTimeStamp = 0;

	/**
	 * Default constructor
	 */
	public InMessage(){
		this.comment = "";
		this.method = "";
		this.path = "";
		this.port = -1;
		this.server = "";
		//this.customFields = new CustomFields();
		this.headers = new Headers();
		this.messageContent = new MessageContent();
		this.postData = new PostData();
		this.protocol = "";
		this.protocolVersion = "";
		this.queryString = new QueryString();
	}
	
	/**
	 * 
	 * @param method HTTP method
	 * @param path 
	 */
	public InMessage(String method, String path){
		this.method = method;
		this.path = path;
	}
	
	/**
	 * Build a InMessage from an HttpServletrequest
	 * @param request The HttpservletRequest
	 */
	//public InMessage(HttpMessRequest request){
	public InMessage(HttpMessageRequestWrapper request){
		
		// TODO : Check this code : WSDL request are not well recorded !
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
		//this.completeUrl = request.getRequestURL().toString();
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
		    requestBodyReader = new BufferedReader(new InputStreamReader(request.getInputStream()));
		    while( requestBodyReader.read(buffer) >= 0 ) {
			requestBody.append( buffer.flip() );
			buffer.clear();
		    }		    
		    this.messageContent.setRawContent(requestBody.toString());
		    this.messageContent.setSize(requestBody.length());
		    this.messageContent.setMimeType(request.getContentType());
		} catch (Exception ex) {
			logger.warn("Error while reading request body !", ex);
		} finally {	    
			try {
				if(requestBodyReader != null){
					requestBodyReader.close();
				}
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

	/*public CustomFields getCustomFields() {
		return customFields;
	}*/

	/*public void setCustomFields(CustomFields customFields) {
		this.customFields = customFields;
	}*/

	public String getPath() {
		return path;
	}
	
	public void setPath(String path){
		this.path = path;
	}

	public MessageContent getMessageContent() {
		return messageContent;
	}

	public void setMessageContent(MessageContent messageContent) {
		this.messageContent = messageContent;
	}	
	
	/**
	 * Builds the complete url
	 * @return the complete url with protocol, server, port and path
	 */
	public String buildCompleteUrl() {
		//return completeUrl;
		StringBuffer urlBuffer = new StringBuffer();
		urlBuffer.append(this.protocol.toLowerCase());
		urlBuffer.append("://");
		urlBuffer.append(this.server);
		if(this.port > -1){
			urlBuffer.append(":");
			urlBuffer.append(this.port);
		}
		urlBuffer.append(this.path);
		/*if(!this.queryString.getQueryParams().isEmpty()){
			urlBuffer.append("?");
			boolean firstParam = true;
			for(QueryParam param : this.queryString.getQueryParams()){
				if(!firstParam){
					urlBuffer.append("?");
				}
				urlBuffer.append(param.getName());
				urlBuffer.append("=");
				urlBuffer.append(param.getValue());
				firstParam = false;
			}
		}*/
		return urlBuffer.toString();
	}

	/**
	 * Get the timestamp when the request has been received
	 * @return
	 */
	public long getRequestTimeStamp() {
		return requestTimeStamp;
	}

	/**
	 * Set the timestamp
	 * @param requestTimeStamp
	 */
	public void setRequestTimeStamp(long requestTimeStamp) {
		this.requestTimeStamp = requestTimeStamp;
	}

	/*public void setCompleteUrl(String completeUrl) {
		this.completeUrl = completeUrl;
	}*/
	
}
