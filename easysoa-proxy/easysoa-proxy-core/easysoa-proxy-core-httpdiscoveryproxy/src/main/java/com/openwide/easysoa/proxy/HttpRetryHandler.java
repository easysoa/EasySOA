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

package com.openwide.easysoa.proxy;

import java.io.IOException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpEntityEnclosingRequest; 
import org.apache.http.NoHttpResponseException;
import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.protocol.HttpContext;
import org.apache.http.protocol.ExecutionContext;
import javax.net.ssl.SSLHandshakeException;

/**
 * HTTP Retry handler for Apache HTTP client used to redirect the request received by the proxy
 * @author jguillemotte
 *
 */
public class HttpRetryHandler implements HttpRequestRetryHandler {

	// Max number of retries
	private int maxRetryNumber;
	
	/**
	 * Default constructor
	 * Recount number is set to 5
	 */
	public HttpRetryHandler(){
		this.maxRetryNumber = 5;
	}
	
	/**
	 * Parameterized constructor
	 * @param maxRetryNumber The max number of retries, must be <= to 0
	 * @throws IllegalArgumentException If the maxNumberRetry param is set with a value < to 0
	 */
	public HttpRetryHandler(int maxRetryNumber) throws IllegalArgumentException {
		if(maxRetryNumber < 0){
			throw new IllegalArgumentException("maxRetryNumber must be at least equals to 0");
		}
		this.maxRetryNumber = maxRetryNumber;
	}
	
	
	public boolean retryRequest(IOException exception, int executionCount, HttpContext context) {
        if (executionCount >= maxRetryNumber) {
            // Do not retry if over max retry count
            return false;
        }
        if (exception instanceof NoHttpResponseException) {
            // Retry if the server dropped connection on us
            return true;
        }
        if (exception instanceof SSLHandshakeException) {
            // Do not retry on SSL handshake exception
            return false;
        }
        HttpRequest request = (HttpRequest) context.getAttribute(ExecutionContext.HTTP_REQUEST);
        boolean idempotent = !(request instanceof HttpEntityEnclosingRequest); 
        if (idempotent) {
            // Retry if the request is considered idempotent 
            return true;
        }
        return false;
	}

}
