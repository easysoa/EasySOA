package org.easysoa.proxy.core.api.event;

import java.util.*;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElementRefs;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author fntangke
 * 
 */

@XmlRootElement(name = "condition")
@XmlAccessorType(XmlAccessType.FIELD)
public class Subscription {
	// Subscriptions [Subscription{ ListenedService{ id, url },
	// LaunchServices[LaunchService{ id, url }]}]
	// Map<List<ListenedService> ,List<LaunchedServices>> content;
	// List<ListenedService> listenedservices;
	/*
	 * TODO mettre ça au dessus de list condition
	 * 
	 * @XmlElementRefs({
	 * 
	 * @XmlElementRef(name="ElementA", type=ClassA),
	 * 
	 * @XmlElementRef(name="ElementB", type=ClassB)}) List<Object> items
	 */

	@XmlElementRefs({
			@XmlElementRef(name = "JXPathCondition", type = JXPathCondition.class),
			@XmlElementRef(name = "RegexCondition", type = RegexCondition.class) })
	private List<Condition> conditions;

	private List<LaunchedService> launchedservices;

	/**
	 * @return a new subscription
	 */
	public Subscription() {
		this.conditions = new ArrayList<Condition>();
		this.launchedservices = new ArrayList<LaunchedService>();
	}

	/**
	 * Create a new Subscription adding the Url in the CompiledCondition
	 * 
	 * @param listenedService
	 */
	public Subscription(ListenedService listenedService) {
		this.conditions = new ArrayList<Condition>();
		this.launchedservices = new ArrayList<LaunchedService>();
		this.addRegexCondition(listenedService.getUrl());
	}

	/**
	 * Constructor set conditions and servicetolaunch in a new Subscription
	 * 
	 * @param conditions
	 * @param serviceToLaunch
	 */
	public Subscription(List<Condition> conditions, List<String> serviceToLaunch) {
		this.conditions = conditions;
		for (String launchServiceUrl : serviceToLaunch) {
			this.addLaunchedService(new LaunchedService(launchServiceUrl));
		}
	}

	/**
	 * @return the launchedservices
	 */
	public List<LaunchedService> getLaunchedservices() {
		return launchedservices;
	}

	/**
	 * @param launchedservices
	 *            the launchedservices to set
	 */
	public void setLaunchedservices(List<LaunchedService> launchedservices) {
		this.setLaunchedservices(launchedservices);
	}

	/**
	 * 
	 * @param launchedService
	 * @return
	 */
	public boolean addLaunchedService(LaunchedService launchedService) {
		return this.launchedservices.add(launchedService);
	}

	/**
	 * 
	 * @return
	 */
	public List<String> getLaunchedServiceUrl() {
		List<String> listLaunchedService = new ArrayList<String>();
		for (LaunchedService launchedService : getLaunchedservices()) {
			listLaunchedService.add(launchedService.getUrl());
		}
		return listLaunchedService;
	}

	/**
	 * Conditions getter
	 * 
	 * @return the compiledContions list
	 */
	public List<Condition> getConditions() {
		return conditions;
	}

	/**
	 * 
	 * @param compiledCondition
	 * @return
	 */
	public boolean addCompiledCondition(Condition compiledCondition) {
		return this.conditions.add(compiledCondition);
	}

	/**
	 * 
	 * @param compiledCondition
	 * @return
	 */
	public boolean removeCompiledCondidtion(Condition compiledCondition) {
		return this.conditions.remove(compiledCondition);
	}

	/**
	 * 
	 * @param regex
	 * @return
	 */
	public boolean addRegexCondition(String regex) {
		return this.addCompiledCondition(new RegexCondition(regex));
	}

	/**
	 * 
	 * @param listenedService
	 * @return
	 */
	public boolean addRegexCondition(ListenedService listenedService) {
		return this.addCompiledCondition(new RegexCondition(listenedService
				.getUrl()));
	}

	public boolean addRegexCondition(RegexCondition regexCondition) {
		return this.conditions.add(regexCondition);
	}

	public List<String> getLaunchedServiceString() {
		List<String> launchList = new ArrayList();
		for (LaunchedService launchedService : this.launchedservices) {
			launchList.add(launchedService.getUrl());
		}
		return launchList;
	}

	/**
	 * Conditions setter
	 * @param conditions
	 */
	public void setConditions(List<Condition> conditions) {
		this.conditions = conditions;
	}

}
