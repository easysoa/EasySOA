/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.easysoa.proxy.core.api.event;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.easysoa.message.InMessage;
import org.easysoa.message.OutMessage;
import org.easysoa.proxy.core.api.configuration.ProxyConfiguration;
import org.easysoa.proxy.core.api.util.RequestForwarder;
import org.easysoa.records.ExchangeRecord;
import org.osoa.sca.annotations.Property;
import org.osoa.sca.annotations.Scope;

/**
 * To handle and perform the steps between the inMessage's reception and the
 * calls (of the web service to launch)
 * 
 * @author fntangke
 */
@Scope("Composite")
public class EventMessageHandlerImpl implements
/* MessageHandler, */IEventMessageHandler {

    //public static final String HANDLER_ID = "EventMessageHandler";    
    
	private ConditionsMatcher conditionsMatcher = new ConditionsMatcher();
	
	@Property
	private int forwardHttpConnexionTimeoutMs;
	@Property
	private int forwardHttpSocketTimeoutMs;
	private Map<List<Condition>, List<String>> listenedServiceUrlToServicesToLaunchUrlMap = new HashMap<List<Condition>, List<String>>();

	public EventMessageHandlerImpl() {

		// RegexCondition rCondition1 = new RegexCondition(
		// "http://localhost:8200/esb/AirportService");
		List<Condition> listConditions = new ArrayList<Condition>();

		List<String> listServiceToCall1 = new ArrayList<String>();
		listServiceToCall1.add("http://www.facebook.fr");
		listServiceToCall1.add("http://www.yahoo.fr");
		Map<List<Condition>, List<String>> listmap = new HashMap<List<Condition>, List<String>>();
		// TODO Arranger JXPah condition
		JXPathCondition jxpCondition = new JXPathCondition("remoteHost",
				"127.0.0.1");

		listConditions.add(jxpCondition);
		listmap.put(listConditions, listServiceToCall1);
		// listCompiledCondition1.add(jxpCondition);
		// listCompiledCondition1.add(rCondition1);

		this.setListenedServiceUrlToServicesToLaunchUrlMap(listmap);
		// Little test of the CompiledCondition
		/*
		 * CompiledCondition compiledCondition = new
		 * CompiledCondition("http://localhost:8200/esb/AirportService");
		 * LaunchedService launchedService = new LaunchedService();
		 * launchedService.setUrl("http://www.google.fr");
		 * 
		 * List<LaunchedService> listCall = new ArrayList<LaunchedService>();
		 * listCall.add(launchedService); Subscription subscription = new
		 * Subscription();
		 * 
		 * subscription.setConditions(conditions);
		 * subscription.setLaunchedservices(listCall);
		 * 
		 * Subscriptions subscriptions = new Subscriptions(subscription);
		 * 
		 * //CompiledCondition compiledCondition1 = new
		 * CompiledCondition("http://localhost:8200/esb/AirportService");
		 * //List<CompiledCondition> listCompiledCondition1 = new
		 * ArrayList<CompiledCondition>();
		 * //listCompiledCondition1.add(compiledCondition1);
		 * 
		 * this.listenedServiceUrlToServicesToLaunchUrlMap =
		 * subscriptions.updateBehaviors(); //.put(listCompiledCondition1,
		 * listCall); // End of Test
		 */
	}

	/**
	 * 
	 * @param inMessage
	 * @param outMessage
	 * @throws Exception
	 * @return Get the web Service to launch and generate valid urls, call them
	 *         and get the result in a list
	 */
	@Override
	public void handleMessage(InMessage inMessage, OutMessage outMessage)
			throws Exception {
		ExchangeRecord exchangeRecord = new ExchangeRecord();
		exchangeRecord.setInMessage(inMessage);
		exchangeRecord.setOutMessage(outMessage);
		/**
		 * listenedServiceUrlToServicesToLaunchUrlMap should be use by one
		 * Thread
		 */
		Collection<Entry<List<Condition>, List<String>>> entrySetCopy;

		synchronized (this) {
			entrySetCopy = new ArrayList<Entry<List<Condition>, List<String>>>(
					listenedServiceUrlToServicesToLaunchUrlMap.entrySet());
		}

		List<String> servicesToLaunchUrls = null; // this.listenedServiceUrlToServicesToLaunchUrlMap.get(listenedServiceUrl);
		// TODO maybe we should remove doublons from the serviceToLaunchsUrls
		for (Entry<List<Condition>, List<String>> currentEntry : entrySetCopy) {
			List<Condition> listCondition = currentEntry.getKey();
			if (conditionsMatcher.matchesAll(listCondition, exchangeRecord)) {
				servicesToLaunchUrls = currentEntry.getValue(); // only the
																// first
																// servicesToLaunch
																// found will be
																// launched
				break;
			}
		}
		if (servicesToLaunchUrls == null) {
			return;
		}

		// TODO LATER handle services that may have different URLs ex. REST

		// Request forwarder
		RequestForwarder forwarder = new RequestForwarder();
		forwarder
				.setForwardHttpConnexionTimeoutMs(getForwardHttpConnexionTimeoutMs());
		forwarder
				.setForwardHttpSocketTimeoutMs(getForwardHttpSocketTimeoutMs());
		// Use HttpRetryHandler default value for retry
		// forwarder.setRetryHandler(new HttpRetryHandler());

		// for each service to call,
		List<OutMessage> launchresults = new ArrayList<OutMessage>();
		for (String serviceToLaunchUrlString : servicesToLaunchUrls) {
			InMessage forwardedInMessage = inMessage; // TODO clone and replace
														// URL etc.
			// URL serviceToLaunchUrl = new URL(serviceToLaunchUrlString);
			// forwardedInMessage.setMethod("GET");
			// forwardedInMessage.setServer("www.facebook.com");
			// forwardedInMessage.setPath("");

			/*
			 * forwardedInMessage.setProtocol(serviceToLaunchUrl.getProtocol());
			 * forwardedInMessage.setServer(serviceToLaunchUrl.getHost());
			 * forwardedInMessage.setPort(serviceToLaunchUrl.getPort());
			 * forwardedInMessage.setPath(serviceToLaunchUrl.getPath()); // TODO
			 * LATER handle services that may have different URLs ex. REST
			 */
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
	 * @param forwardHttpConnexionTimeoutMs
	 *            the forwardHttpConnexionTimeoutMs to set
	 */
	public void setForwardHttpConnexionTimeoutMs(
			int forwardHttpConnexionTimeoutMs) {
		this.forwardHttpConnexionTimeoutMs = forwardHttpConnexionTimeoutMs;
	}

	/**
	 * @return the forwardHttpSocketTimeoutMs
	 */
	public int getForwardHttpSocketTimeoutMs() {
		return forwardHttpSocketTimeoutMs;
	}

	/**
	 * @param forwardHttpSocketTimeoutMs
	 *            the forwardHttpSocketTimeoutMs to set
	 */
	public void setForwardHttpSocketTimeoutMs(int forwardHttpSocketTimeoutMs) {
		this.forwardHttpSocketTimeoutMs = forwardHttpSocketTimeoutMs;
	}

	@Override
	public Map<List<Condition>, List<String>> getListenedServiceUrlToServicesToLaunchUrlMap() {
		return listenedServiceUrlToServicesToLaunchUrlMap;
	}

	@Override
	public void setListenedServiceUrlToServicesToLaunchUrlMap(
			Map<List<Condition>, List<String>> listenedServiceUrlToServicesToLaunchUrlMap) {
		synchronized (this) {
			this.listenedServiceUrlToServicesToLaunchUrlMap = listenedServiceUrlToServicesToLaunchUrlMap;
		}
	}

	@Override
	public boolean isApplicable(InMessage inMessage) {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public Configuration getConfiguration() {
		Configuration conf = new Configuration();
		for (Entry<List<Condition>, List<String>> entry : this.listenedServiceUrlToServicesToLaunchUrlMap
				.entrySet()) {
			List<Condition> conditions = entry.getKey();
			List<String> servicesToCall = entry.getValue();
			// TODO Add a subscriptions
			conf.addSubscription(new Subscription(conditions, servicesToCall));
		}
		return conf;
	}

    @Override
    public void setHandlerConfiguration(ProxyConfiguration configuration) {
        // Nothing to do
    }

    /*@Override
    public String getID() {
        return HANDLER_ID;
    }*/
}
