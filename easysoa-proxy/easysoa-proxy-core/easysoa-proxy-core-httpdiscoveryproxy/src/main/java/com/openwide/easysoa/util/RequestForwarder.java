/**
 * 
 */
package com.openwide.easysoa.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.Date;

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
import com.openwide.easysoa.message.Header;
import com.openwide.easysoa.message.InMessage;
import com.openwide.easysoa.message.MessageContent;
import com.openwide.easysoa.message.OutMessage;
import com.openwide.easysoa.message.QueryParam;

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
	private OutMessage sendSoapRequest(InMessage inMessage){
		return null;
	}
	
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
	    	for(QueryParam queryParam : inMessage.getQueryString().getQueryParams()){
	    		// for each query param, build name=value and add '&' char
	    		if("?".equals(requestUrlBuffer.charAt(requestUrlBuffer.length()-1))){
		    		requestUrlBuffer.append("&");
	    		}
	    		requestUrlBuffer.append(queryParam.getName());
	    		requestUrlBuffer.append("=");
	    		requestUrlBuffer.append(queryParam.getValue());
	    	}
	    }
		logger.debug("URL : " + requestUrlBuffer.toString());
	    	
		// message body
    	HttpEntity httpEntity = new StringEntity(inMessage.getMessageContent().getContent());
		
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
		HttpResponse clientResponse = httpClient.execute(httpUriRequest);
		Date responseSendDate = new Date();
    	
    	// Get and package the response
    	// TODO set the missing value like timnings ....
    	OutMessage outMessage = new OutMessage(clientResponse.getStatusLine().getStatusCode(), clientResponse.getStatusLine().getReasonPhrase());
		inMessage.setRequestTimeStamp(requestSendDate.getTime());
    	outMessage.setResponseTimeStamp(responseSendDate.getTime());
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
		messageContent.setContent(responseBuffer.toString());
    	messageContent.setSize(clientResponse.getEntity().getContentLength());
    	if(clientResponse.getEntity().getContentType() != null){
    		messageContent.setMimeType(clientResponse.getEntity().getContentType().getValue());
    	}
    	outMessage.setMessageContent(messageContent);    	
    	// Return response message
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
			// to avoid an exception when the Content-length header is set twice
			// if("Host".equals(headerName) &&
			// headerValue.contains("microsoft")){////
			// httpMessage.setHeader("Host", "localhost:8084");////
			// } else/////
			if (!"Content-Length".equals(header.getName()) && !"Transfer-Encoding".equals(header.getName())) {
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
