package com.openwide.easysoa.esperpoc.esper;

import org.apache.log4j.Logger;
import com.espertech.esper.client.EventBean;
import com.espertech.esper.client.UpdateListener;
import com.openwide.easysoa.esperpoc.NuxeoRegistrationService;
import com.openwide.easysoa.esperpoc.UrlTreeNode;
import com.openwide.easysoa.esperpoc.UrlTreeNodeEvent;

public class UrlTreeEventListener implements UpdateListener {

	/**
	 * Logger
	 */
	static Logger logger = Logger.getLogger(UrlTreeEventListener.class.getName());
	
	@Override
	public void update(EventBean[] newData, EventBean[] oldData) {
		logger.debug("[update()] new event received !");
		// Each time an event is received
		// An analysis must be done to determine if a new service can be registered in Nuxeo ....
		logger.debug("[MessageListener] --- NewData length : " + newData.length);
		logger.debug("[MessageListener] --- Event received : " + newData[0].getUnderlying());
		logger.debug("[MessageListener] --- " + newData[0].getUnderlying().getClass().getName());
		NuxeoRegistrationService nrs = new NuxeoRegistrationService();
		Service service;
		UrlTreeNodeEvent event = (UrlTreeNodeEvent)(newData[0].getUnderlying());
		UrlTreeNode node = event.getEventSource();
		
		// Detecting API / Application / Services 
		// Analyse ratio, Depth ....
		// Esper request or Java algorithm ????  
		
		// Application => direct childs of root node
		// API => level 2 childs to level n childs 
		// Atomic service => level n+1 to level n+n
		
		// Register applications
		/*{
			  "description": "Notification concerning an application implementation.",
			  "parameters": {
			    "rootServicesUrl": "(mandatory) Services root.",
			    "uiUrl": "Application GUI entry point.",
			    "title": "The name of the document.",
			    "technology": "Services implementation technology.",
			    "standard": "Protocol standard if applicable.",
			    "sourcesUrl": "Source code access.",
			    "description": "A short description.",
			    "server": "IP of the server."
			  }
			}*/
		//nrs.registerRestService(service);
	}

}
