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

package com.openwide.sca.intents;

import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.osoa.sca.ServiceUnavailableException;
import org.osoa.sca.annotations.Scope;
import org.osoa.sca.annotations.Service;
import org.ow2.frascati.tinfi.api.IntentHandler;
import org.ow2.frascati.tinfi.api.IntentJoinPoint;
import com.openwide.sca.intents.utils.RequestElement;
//import javax.servlet.http.HttpServletRequest;
//import org.eclipse.jetty.server.Request;

@Scope("COMPOSITE")
@Service(IntentHandler.class)
public class AutoRearmFuseIntent implements IntentHandler {

    // Logger
    private static Log log = LogFactory.getLog(AutoRearmFuseIntent.class);    
    
	/**
	 * Max number of requests in a given time period (default value)
	 */
	//@Property
	protected int maxRequestsNumber = 10;
	/** 
	 * Period of time during the requests number must not be > to the
	 * maxRequestNumber (in Ms) (default value)
	 */
	//@Property
	protected int maxTimePeriod = 120000;
	
	/**
	 * The queue to stack the requests
	 */
	private Queue<RequestElement> requestQueue = null;

	/**
	 * Constructor
	 */
	public AutoRearmFuseIntent() {
		//System.out.println("[AUTOREARMFUSE INTENT] AUTOREARMFUSE INTENT default Constructor !!");
		// Queue initialization -> max size = maxRequestNumber
		requestQueue = new ArrayBlockingQueue<RequestElement>(maxRequestsNumber);
	}

	/** Set the maxRequestsNumber property. */
	public void setMaxRequestsNumber(final String maxRequestsNumber) {
		//System.out.println("[AUTOREARMFUSE INTENT] : setting maxRequestsNumber to '" + maxRequestsNumber + "'.");
	    log.info("[AUTOREARMFUSE INTENT] : setting maxRequestsNumber to '" + maxRequestsNumber + "'.");
		try {
			int mrn = Integer.parseInt(maxRequestsNumber);
			if (mrn > 0) {
				this.maxRequestsNumber = mrn;
				requestQueue = new ArrayBlockingQueue<RequestElement>(
						this.maxRequestsNumber);
			}
		} catch (Exception ex) {
			//System.out.println("[AUTOREARMFUSE INTENT] : Invalid value for maxRequestsNumber property. Default value will be use instead !");
			log.error("[AUTOREARMFUSE INTENT] : Invalid value for maxRequestsNumber property. Default value will be use instead !");
		}
	}

	/** Set the maxTimePeriod property. */
	public void setMaxTimePeriod(final String maxTimePeriod) {
		//System.out.println("[AUTOREARMFUSE INTENT] : setting maxTimePeriod to '" + maxTimePeriod + "'.");
		log.info("[AUTOREARMFUSE INTENT] : setting maxTimePeriod to '" + maxTimePeriod + "'.");
		try {
			int mtp = Integer.parseInt(maxTimePeriod);
			if (mtp > 500) {
				this.maxTimePeriod = mtp;
			}
		} catch (Exception ex) {
			//System.out.println("[AUTOREARMFUSE INTENT] : Invalid value for maxTimePeriod property. Default value will be use instead !");
			log.error("[AUTOREARMFUSE INTENT] : Invalid value for maxTimePeriod property. Default value will be use instead !");
			
		}
	}
	
	/**
	 * Check if the request respect the conditions
	 * 
	 * @throws Throwable
	 *             If the request does not respect the conditions
	 */
	public void checkFuseConditions() throws ServiceUnavailableException, Throwable {
		// get the current time in Ms
		long currentTime = System.currentTimeMillis();
		//System.out.println("[AUTOREARMFUSE INTENT] Current time (Ms) : " + currentTime);
		log.debug("[AUTOREARMFUSE INTENT] Current time (Ms) : " + currentTime);
		//System.out.println("[AUTOREARMFUSE INTENT] Requests in queue : " + requestQueue.size());
		log.debug("[AUTOREARMFUSE INTENT] Requests in queue : " + requestQueue.size());
		if(requestQueue.size() > 0){
			//System.out.println("[AUTOREARMFUSE INTENT] Older request time in queue : " + requestQueue.peek().getRequestTime());
			log.debug("[AUTOREARMFUSE INTENT] Older request time in queue : " + requestQueue.peek().getRequestTime());
		}
		// if the queue is not full, add the new request in the queue
		if(requestQueue.size() < maxRequestsNumber){
			requestQueue.add(createRequestElement(currentTime));
		} 
		// Otherwise check if the difference between the oldest element time and the current time is > to the maxTimePeriod
		else {
			RequestElement olderRequest = requestQueue.peek();
			// if true, add the new request in the queue
			if((currentTime - olderRequest.getRequestTime()) > maxTimePeriod){
				requestQueue.poll();
				requestQueue.add(createRequestElement(currentTime));
			}
			// otherwise, throw an exception
			else {
				String errorMessage = "[AUTOREARMFUSE INTENT] Too much requests detected for the time period, this resquest has been blocked !";
				//System.out.println(errorMessage);
				log.error(errorMessage);
				throw new ServiceUnavailableException(errorMessage);
			}
		}
	}

	/*
	 * Create a request element
	 */
	private RequestElement createRequestElement(long currentTime)
			throws IllegalArgumentException {
		return new RequestElement("request-" + currentTime, currentTime);
	}

	/**
	 * Implementation of the IntentHandler interface
	 * 
	 * @see org.ow2.frascati.tinfi.control.intent.IntentHandler#invoke(IntentJoinPoint)
	 */
	public Object invoke(IntentJoinPoint ijp) throws Throwable {
		Object ret;
		Object[] args = ijp.getArguments();
		//HttpServletRequest request = (HttpServletRequest)args[1];
		//Request request = (Request)args[1];
		
		//System.out.println("Entering invoke method !");
		// PUT HERE CODE TO RUN BEFORE THE JOINPOINT PROCESSING
		//try{
		    checkFuseConditions();
		//}
		//catch(ServiceUnavailableException ex){
		    // This exception is returned if the fuse max request limit is reached
		    // Send a error response
		    //request.getResponse().sendError(500, ex.getMessage());
		//}
		
		ret = ijp.proceed();
		// PUT HERE CODE TO RUN AFTER THE JOINPOINT PROCESSING
		//System.out.println("Exiting invoke method !");
		return ret;
	}
}