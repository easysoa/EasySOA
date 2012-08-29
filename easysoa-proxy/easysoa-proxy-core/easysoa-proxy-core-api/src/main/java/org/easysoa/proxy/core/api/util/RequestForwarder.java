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

package org.easysoa.proxy.core.api.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Date;
import java.util.HashSet;

import org.apache.http.HttpEntity;
import org.apache.http.HttpMessage;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpOptions;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.log4j.Logger;
import org.easysoa.message.Header;
import org.easysoa.message.InMessage;
import org.easysoa.message.MessageContent;
import org.easysoa.message.OutMessage;
import org.easysoa.message.QueryParam;

/**
 * This class contains stuff to help with forwarding a request
 * It use an Apache HTTP client to send the request. 
 * @author jguillemotte
 */
public class RequestForwarder {

	/**
	 * Logger
	 */
	static Logger logger = Logger.getLogger(RequestForwarder.class.getName());	
	
	// Retry handler
	private HttpRequestRetryHandler retryHandler; 
	// Timeouts
	private int forwardHttpConnexionTimeoutMs;
	private int forwardHttpSocketTimeoutMs;

    @SuppressWarnings("serial")
    private static HashSet<String> autoSetHeaderLowerCaseSet = new HashSet<String>() { {
        add("content-length");
        add("transfer-encoding");
    } };
	
	/**
	 * Default constructor
	 */
	public RequestForwarder(){
		forwardHttpConnexionTimeoutMs = -1;
		forwardHttpSocketTimeoutMs = -1;
	}
	
	/**
	 * send the HTTP REST request, build an <code>OutMessage</code> with the response
	 * @param inMessage The request will be build with this message 
	 * @throws IOException, ClientProtocolException If a problem occurs 
	 */
	// TODO : add a return type corresponding to the response
	// TODO : works only with HTTP REST Request, need another implementation for SOAP
	public OutMessage send(InMessage inMessage) throws ClientProtocolException, IOException{
	//public OutMessage send(Exchange type, InMessage inMessage) throws ClientProtocolException, IOException{		
		// Case of REST request
		/*if(inMessage.get){*/
			return sendRestRequest(inMessage);
		/*} 
		// Case of SOAP request
		else if(){
			return sendSoapRequest();
		} 
		// Unable to find the appriopriate sender
		else {
			throw new Exception("Unable to send request, unknow message type !");
		}*/
	}

	/**
	 * Send a SOAP request
	 * @param inMessage The request to send
	 * @return <code>OutMessage</code> the response as an OutMessage object
	 */
	/*private OutMessage sendSoapRequest(InMessage inMessage){
		return null;
	}*/
	
	/**
	 * Send a REST request
	 * @param inMessage The request to send
	 * @return <code>OutMessage</code> the response as an OutMessage object
	 * @throws IOException 
	 * @throws ClientProtocolException 
	 */
	private OutMessage sendRestRequest(InMessage inMessage) throws ClientProtocolException, IOException{
		// Default HTTP client
		DefaultHttpClient httpClient = new DefaultHttpClient();
		// Set retry handler
		if(retryHandler != null){
			httpClient.setHttpRequestRetryHandler(retryHandler);
		}
		// set the connection timeout
		HttpParams httpParams = httpClient.getParams();
		if(forwardHttpConnexionTimeoutMs > 0){
			HttpConnectionParams.setConnectionTimeout(httpParams, this.forwardHttpConnexionTimeoutMs);
		}
		if(forwardHttpSocketTimeoutMs > 0){
			HttpConnectionParams.setSoTimeout(httpParams, this.forwardHttpSocketTimeoutMs);
		}
		// Build URL with parameters
		StringBuffer requestUrlBuffer = new StringBuffer();
		requestUrlBuffer.append(inMessage.buildCompleteUrl());
	    if(inMessage.getQueryString() != null){
	    	requestUrlBuffer.append("?");
	    	// TODO In case of POST Method, the params have to be in the message content, not in the query itself !
    		boolean firstParam = true;
	    	for(QueryParam queryParam : inMessage.getQueryString().getQueryParams()){
	    		// for each query param, build name=value and add '&' char
	    		if(!firstParam){
		    		requestUrlBuffer.append("&");
	    		}
	    		requestUrlBuffer.append(queryParam.getName());
	    		requestUrlBuffer.append("=");
	    		requestUrlBuffer.append(queryParam.getValue().replace(" ", "%20"));
	    		firstParam = false;
	    	}
	    }
		logger.debug("URL : " + requestUrlBuffer.toString());
	    	
		// message body
    	HttpEntity httpEntity = new StringEntity(inMessage.getMessageContent().getRawContent());
		
		HttpUriRequest httpUriRequest;
		// TODO later use a pattern to create them (builder found in a map method -> builder...)
		if("GET".equalsIgnoreCase(inMessage.getMethod())){
	    	httpUriRequest = new HttpGet(requestUrlBuffer.toString());		
		} else if("PUT".equalsIgnoreCase(inMessage.getMethod())){
	 	   	HttpPut httpPut = new HttpPut(requestUrlBuffer.toString());
	 	   	httpPut.setEntity(httpEntity);
	 	   	httpUriRequest = httpPut;
	 	} else if("DELETE".equalsIgnoreCase(inMessage.getMethod())){
	 	   	httpUriRequest = new HttpDelete(requestUrlBuffer.toString());
	 	} else if("OPTIONS".equalsIgnoreCase(inMessage.getMethod())){
	 	 	httpUriRequest = new HttpOptions(requestUrlBuffer.toString());
	 	} else if("HEAD".equalsIgnoreCase(inMessage.getMethod())){
	 	   	httpUriRequest = new HttpOptions(requestUrlBuffer.toString());
	 	} else if("TRACE".equalsIgnoreCase(inMessage.getMethod())){
	 	  	httpUriRequest = new HttpOptions(requestUrlBuffer.toString());
	 	} else { // POST
	 	   	HttpPost httpPost = new HttpPost(requestUrlBuffer.toString());
	 	   	httpPost.setEntity(httpEntity);
	 	   	httpUriRequest = httpPost;
	 	}
		setHeaders(inMessage, httpUriRequest);
		
		// Send the request
		Date requestSendDate = new Date();
        inMessage.setRequestTimeStamp(requestSendDate.getTime());
		HttpResponse clientResponse = httpClient.execute(httpUriRequest);
    	
    	// Get and package the response
    	// TODO set the missing value like timnings ....
    	OutMessage outMessage = buildOutMessage(clientResponse);
    	
    	// Return response message
		return outMessage;
	}
	
	private OutMessage buildOutMessage(HttpResponse clientResponse) throws IllegalStateException, IOException {
        Date responseSendDate = new Date();
        OutMessage outMessage = new OutMessage(clientResponse.getStatusLine().getStatusCode(), clientResponse.getStatusLine().getReasonPhrase());
        outMessage.setResponseTimeStamp(responseSendDate.getTime());
        
        // copy content
        
        MessageContent messageContent = new MessageContent();
        
        // Read the response message content
        InputStreamReader in= new InputStreamReader(clientResponse.getEntity().getContent());
        BufferedReader bin= new BufferedReader(in);
        StringBuffer responseBuffer = new StringBuffer();
        String line;
        do{
             line = bin.readLine();
             if(line != null){
                 responseBuffer.append(line);
             }
        }
        while(line != null);
        messageContent.setRawContent(responseBuffer.toString());
        messageContent.setSize(clientResponse.getEntity().getContentLength());
        if(clientResponse.getEntity().getContentType() != null){
            messageContent.setMimeType(clientResponse.getEntity().getContentType().getValue());
        }
        outMessage.setMessageContent(messageContent);
        // TODO : There is an encoding problem when returning the response and when the encoding is not ISO-8859 ....
        //messageContent.setEncoding(clientResponse.getEntity().getContentEncoding().getValue());
        org.apache.http.Header contentEncoding = clientResponse.getEntity().getContentEncoding();
        if (contentEncoding != null) {
            messageContent.setEncoding(contentEncoding.getValue());
        }
        
        // copy updated headers
        for (org.apache.http.Header header : clientResponse.getAllHeaders()) {
            String headerName = header.getName();
            String headerValue = header.getValue();
            
            // handling headers computed from content (may have been modified (?!))
            // see http://en.wikipedia.org/wiki/List_of_HTTP_header_fields#Responses
            String headerLowercaseName = headerName.toLowerCase();
            if ("content-length".equals(headerLowercaseName)) { // Content-Length
                headerValue = String.valueOf(clientResponse.getEntity().getContentLength());
            } else if ("content-encoding".equals(headerLowercaseName)) { // Content-Length
                headerValue = String.valueOf(clientResponse.getEntity().getContentEncoding());
            } // else TODO others 
            outMessage.getHeaders().addHeader(new Header(headerName, headerValue ));
        }
        
        return outMessage;
    }

    /**
	 * Set headers in the httpMessage
	 * @param request The request where to get headers
	 * @param httpMessage The http message to set
	 */
	private void setHeaders(InMessage inMessage, HttpMessage httpMessage) {
		//logger.debug("Requests Headers :");
		for (Header header : inMessage.getHeaders().getHeaderList()) {
			// Don't set again automatically set headers (else ex. Content-Length set twice)
		    // Moreover, RFC says that headers are case-insensitive
			if (!autoSetHeaderLowerCaseSet.contains(header.getName().toLowerCase())) {
				httpMessage.setHeader(header.getName(), header.getValue());
			}
			logger.debug(header.getName() + ": " + header.getValue());
		}
	}	    
	    
	/**
	 * Set the retry handler
	 * @param retryHandler 
	 */
	public void setRetryHandler(HttpRequestRetryHandler retryHandler){
		this.retryHandler = retryHandler;
	}
	
	/**
	 * Get the retry handler
	 * @return <code>HttpRetryHandler</code>
	 */
	public HttpRequestRetryHandler getRetryHandler(){
		return retryHandler;
	}
	
	/**
	 * Returns the http connexion timeout in MS
	 * @return http connexion timeout in Ms
	 */
	public int getForwardHttpConnexionTimeoutMs() {
		return forwardHttpConnexionTimeoutMs;
	}

	/**
	 * Set the http connexion timeout in Ms
	 * @param forwardHttpConnexionTimeoutMs Specified in milliseconds
	 */
	public void setForwardHttpConnexionTimeoutMs(int forwardHttpConnexionTimeoutMs) {
		this.forwardHttpConnexionTimeoutMs = forwardHttpConnexionTimeoutMs;
	}

	/**
	 * Returns the http socket timeout in MS
	 * @return http socket timeout in Ms
	 */
	public int getForwardHttpSocketTimeoutMs() {
		return forwardHttpSocketTimeoutMs;
	}

	/**
	 * Set the http socket timeout in Ms
	 * @param forwardHttpSocketTimeoutMs Specified in milliseconds
	 */
	public void setForwardHttpSocketTimeoutMs(int forwardHttpSocketTimeoutMs) {
		this.forwardHttpSocketTimeoutMs = forwardHttpSocketTimeoutMs;
	}
	
}
