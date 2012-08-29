package org.easysoa.proxy.core.api.event;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author fntangke
 */

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class Configuration {

	@XmlElement(name = "subscriptions")
	private List<Subscription> subscriptions;

	private String proxy;

	public Configuration() {
		this.subscriptions = new ArrayList<Subscription>();
	}

	public Configuration(Subscription subscription) {
		this.subscriptions = new ArrayList<Subscription>();
		this.subscriptions.add(subscription);
	}

	public Configuration(List<Subscription> subscription) {
		this.subscriptions = subscription;
	}

	public String getProxy() {
		return proxy;
	}

	/**
	 * proxy setter
	 * 
	 * @param proxy
	 */
	public void setProxy(String proxy) {
		this.proxy = proxy;
	}

	public List<Subscription> getSubscriptions() {
		return subscriptions;
	}

	public void setSubscriptions(List<Subscription> subscriptions) {
		this.subscriptions = subscriptions;
	}

	/**
	 * @param behavior
	 * @return Set the behavior when a listened web service is called
	 */
	public Map<List<Condition>, List<String>> updateBehaviors() {
		Map<List<Condition>, List<String>> behavior = new HashMap<List<Condition>, List<String>>();
		List<Condition> listCompiledCondition = new ArrayList<Condition>();
		for (Subscription subscription : this.subscriptions) {
			// TODO revoir comment ceer des JXPath Condition et ou seront elles
			// creer.
			// CompiledCondition compiledCondition = new
			// CompiledCondition(subscription.getListenedService().getUrl());
			// listCompiledCondition.add(compiledCondition);
			// behavior.put(listCompiledCondition,
			// subscription.getLaunchedServiceUrl());
		}
		return behavior;
	}

	/**
	 * Add a subscription to the condition list
	 * 
	 * @param subscription
	 * @return
	 */
	public boolean addSubscription(Subscription subscription) {
		return this.subscriptions.add(subscription);
	}
}
