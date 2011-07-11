package com.openwide.easysoa.esperpoc;

import java.io.IOException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpEntityEnclosingRequest; 
import org.apache.http.NoHttpResponseException;
import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.protocol.HttpContext;
import org.apache.http.protocol.ExecutionContext;
import javax.net.ssl.SSLHandshakeException;

/**
 * Http Retry handler for Apache HTTP client used to redirect the request received by the proxy
 * @author jguillemotte
 *
 */
public class HttpRetryHandler implements HttpRequestRetryHandler {

	private static final int MAX_RETRY_NUMBER = 5; 
	
	@Override
	public boolean retryRequest(IOException exception, int executionCount, HttpContext context) {
        if (executionCount >= MAX_RETRY_NUMBER) {
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
