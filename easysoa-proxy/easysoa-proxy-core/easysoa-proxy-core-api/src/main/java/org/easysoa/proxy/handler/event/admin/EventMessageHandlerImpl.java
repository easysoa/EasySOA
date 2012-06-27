/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.easysoa.proxy.handler.event.admin;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpHost;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.params.ConnRoutePNames;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.easysoa.EasySOAConstants;
import org.easysoa.proxy.handler.event.*;
import org.osoa.sca.annotations.Property;
import org.osoa.sca.annotations.Reference;
import org.osoa.sca.annotations.Scope;

import com.espertech.esper.epl.generated.EsperEPL2GrammarParser.betweenList_return;
import com.openwide.easysoa.exchangehandler.MessageHandler;
import com.openwide.easysoa.message.InMessage;
import com.openwide.easysoa.message.OutMessage;
import com.openwide.easysoa.util.RequestForwarder;

/**
 * To handle and perform the steps between the inMessage's reception and the calls (of the web service to launch) 
 * @author fntangke
 */

@Scope("Composite")
public class EventMessageHandlerImpl implements MessageHandler, IEventMessageHandler {

	@Property
    private int forwardHttpConnexionTimeoutMs;	
    @Property
	private int forwardHttpSocketTimeoutMs;
    
	private Map<String, List<String>> listenedServiceUrlToServicesToLaunchUrlMap = new HashMap<String, List<String>>();
    
    public EventMessageHandlerImpl(){
    }
    
    /**
     * Get the web Service to launch and generate valid urls, call them and get the result in a list
     */
    @Override
    public void handleMessage(InMessage inMessage, OutMessage outMessage) throws Exception {
        
        // find listened service in map among n
        String listenedServiceUrl = inMessage.buildCompleteUrl();

        /**
         * listenedServiceUrlToServicesToLaunchUrlMap should be use by one Thread
         */
        List<String> servicesToLaunchUrls;
    	synchronized(this) {
            List<String> servicesToLaunchUrlsOrig = this.listenedServiceUrlToServicesToLaunchUrlMap.get(listenedServiceUrl);
	        if (servicesToLaunchUrlsOrig == null) {
	                return;
	        }
	        servicesToLaunchUrls = new ArrayList<String>(servicesToLaunchUrlsOrig);
    	}

        // TODO LATER handle services that may have different URLs ex. REST

        // Request forwarder
        RequestForwarder forwarder = new RequestForwarder();
        forwarder.setForwardHttpConnexionTimeoutMs(getForwardHttpConnexionTimeoutMs());
        forwarder.setForwardHttpSocketTimeoutMs(getForwardHttpSocketTimeoutMs());
        // Use HttpRetryHandler default value for retry
        //forwarder.setRetryHandler(new HttpRetryHandler());

        // for each service to call,
        List<OutMessage> launchresults = new ArrayList<OutMessage>();
        for (String serviceToLaunchUrlString : servicesToLaunchUrls){
            InMessage forwardedInMessage = inMessage; // TODO clone and replace URL etc.
            URL serviceToLaunchUrl = new URL(serviceToLaunchUrlString);
            forwardedInMessage.setProtocol(serviceToLaunchUrl.getProtocol());
            forwardedInMessage.setServer(serviceToLaunchUrl.getHost());
            forwardedInMessage.setPort(serviceToLaunchUrl.getPort());
            forwardedInMessage.setPath(serviceToLaunchUrl.getPath()); // TODO LATER handle services that may have different URLs ex. REST

            // call it TODO LATER async using a threaded Queue Worker     
            OutMessage forwardedOutMessage = forwarder.send(forwardedInMessage);
            
            // Save the result
            launchresults.add(forwardedOutMessage);
        }
    }

    
    /**
     * @return the forwardHttpConnexionTimeoutMs
     */
    public int getForwardHttpConnexionTimeoutMs() {
        return forwardHttpConnexionTimeoutMs;
    }

    /**
     * @param forwardHttpConnexionTimeoutMs the forwardHttpConnexionTimeoutMs to set
     */
    public void setForwardHttpConnexionTimeoutMs(int forwardHttpConnexionTimeoutMs) {
        this.forwardHttpConnexionTimeoutMs = forwardHttpConnexionTimeoutMs;
    }

    /**
     * @return the forwardHttpSocketTimeoutMs
     */
    public int getForwardHttpSocketTimeoutMs() {
        return forwardHttpSocketTimeoutMs;
    }

    /**
     * @param forwardHttpSocketTimeoutMs the forwardHttpSocketTimeoutMs to set
     */
    public void setForwardHttpSocketTimeoutMs(int forwardHttpSocketTimeoutMs) {
        this.forwardHttpSocketTimeoutMs = forwardHttpSocketTimeoutMs;
    }

    /**
     * READONLY ! else concurrency pb
     * @return the listenedServiceUrlToServicesToLaunchUrlMap
     */
    public Map<String, List<String>> getListenedServiceUrlToServicesToLaunchUrlMap() {
        return this.listenedServiceUrlToServicesToLaunchUrlMap;
    }

    /**
     * synchronized
     * @param listenedServiceUrlToServicesToLaunchUrlMap the listenedServiceUrlToServicesToLaunchUrlMap to set
     */
    public void setListenedServiceUrlToServicesToLaunchUrlMap(Map<String, List<String>> listenedServiceUrlToServicesToLaunchUrlMap) {
    	synchronized(this) {
    		this.listenedServiceUrlToServicesToLaunchUrlMap = listenedServiceUrlToServicesToLaunchUrlMap;
    	}
    }
 
}
