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

	private String protocol;
	
	private String method;
	private String server;
	private int port;
	private String path;
	private String httpVersion;
	private Headers headers;
	private QueryString queryString;
	private PostData postData;
	// private Long headersSize;
	// private Long bodySize;
	private String comment;
	private CustomFields customFields = new CustomFields();

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
		this.protocol = request.getProtocol();
		this.server = request.getServerName();
		this.port = request.getServerPort();
		this.path = request.getServletPath();
		// Set url parameters
		this.queryString = new QueryString();
		Enumeration<String> parametersNameEnum = request.getParameterNames(); 
		while(parametersNameEnum.hasMoreElements()){
			String parameterName = parametersNameEnum.nextElement();
			for(String parameterValue : request.getParameterValues(parameterName)){
				this.queryString.addQueryParam(new QueryParam(parameterName, parameterValue));
			}
		}
		this.comment = "";
		//logger.debug("InMessage done");
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

	public String getHttpVersion() {
		return httpVersion;
	}

	public void setHttpVersion(String httpVersion) {
		this.httpVersion = httpVersion;
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

}
