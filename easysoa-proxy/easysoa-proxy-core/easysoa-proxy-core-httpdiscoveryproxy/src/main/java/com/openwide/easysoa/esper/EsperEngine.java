package com.openwide.easysoa.esper;

import com.openwide.easysoa.monitoring.Message;
import com.openwide.easysoa.monitoring.soa.Node;

public interface EsperEngine {

	/**
	 * Send a event to the Esper engine 
	 * @param soaNode The <code>Node</code> contained in the event
	 */
	public void sendEvent(Node soaNode);

	/**
	 * Send a event to the Esper engine 
	 * @param soaNode The <code>Message</code> contained in the event
	 */
	public void sendEvent(Message message);	
	
}
