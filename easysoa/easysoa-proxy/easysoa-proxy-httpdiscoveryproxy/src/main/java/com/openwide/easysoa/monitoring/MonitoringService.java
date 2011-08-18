package com.openwide.easysoa.monitoring;

import java.util.ArrayDeque;

import com.openwide.easysoa.monitoring.apidetector.UrlTree;

public interface MonitoringService {

	/**
	 * Modes
	 */
	public enum MonitoringMode {
		DISCOVERY, VALIDATED
	}	
	
	/**
	 * Listen a message
	 * @param message The <code>Message</code> to listen
	 */
	public void listen(Message message);

	/**
	 * 
	 * @return
	 */
	public MonitoringModel getModel();

	/**
	 * Returns the url tree
	 * @return
	 */
	public UrlTree getUrlTree();

	/**
	 * Returns the unknown messages list
	 * @return
	 */
	public ArrayDeque<Message> getUnknownMessagesList();

	/**
	 */
	public void registerUnknownMessagesToNuxeo();

	/**
	 * Sends detected apis & services to nuxeo
	 * Analyse the urlTree
	 */
	public void registerDetectedServicesToNuxeo();

}