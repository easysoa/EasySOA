package com.openwide.easysoa.monitoring;

import com.openwide.easysoa.esper.EsperEngine;

public interface MessageHandler {
	
	/**
	 * return if the message can be handled by this handler
	 * @param message The message to handle 
	 * @return True if the message can be handled, false otherwise
	 */
	public boolean isOkFor(Message message);
	
	/**
	 * Handle the message
	 * @param message The message to handle
	 */
	// TODO : Monitoring service as parameter here is not a good solution, find an other way ...
	public boolean handle(Message message, MonitoringService monitoringService, EsperEngine esperEngine);

}
