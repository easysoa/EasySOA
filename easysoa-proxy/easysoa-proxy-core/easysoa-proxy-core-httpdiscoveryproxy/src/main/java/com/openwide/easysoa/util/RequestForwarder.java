/**
 * 
 */
package com.openwide.easysoa.util;


import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.easysoa.records.ExchangeRecord;

import com.openwide.easysoa.message.InMessage;
import com.openwide.easysoa.proxy.HttpRetryHandler;

/**
 * This class contains stuff to help with forwarding a request
 * It use an Apache HTTP client to send the request. 
 * @author jguillemotte
 */
public class RequestForwarder {

	// Retry handler
	private HttpRequestRetryHandler retryHandler; 
	// Timeouts
	private int forwardHttpConnexionTimeoutMs = -1;
	private int forwardHttpSocketTimeoutMs = -1;
	
	public RequestForwarder(){
		
	}

	/**
	 * Forward the request
	 */
	// TODO : add a return type corresponding to the response
	public void send(ExchangeRecord exchangeRecord){
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
		
		// Building URL
		/*InMessage inMessage = exchangeRecord.getInMessage();
		StringBuffer requestUrlBuffer = new StringBuffer();
		requestUrlBuffer.append(inMessage.getProtocol());
	    if(request.getQueryString() != null){
	    	requestUrlBuffer.append("?");
	    	requestUrlBuffer.append(request.getQueryString());
	    }
	    String requestUrlString = requestUrlBuffer.toString();*/
	    
	    /*
	    HttpEntity httpEntity = new StringEntity(message.getBody());
		HttpUriRequest httpUriRequest;		
		*/
		
		
		
	}

	/**
	 * 
	 * @param retryHandler
	 */
	public void setRetryHandler(HttpRequestRetryHandler retryHandler){
		this.retryHandler = retryHandler;
	}
	
	/**
	 * 
	 * @return
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
