package com.openwide.easysoa.esper;

import org.apache.log4j.Logger;
import com.espertech.esper.client.EventBean;
import com.espertech.esper.client.UpdateListener;
import com.openwide.easysoa.monitoring.soa.Api;
import com.openwide.easysoa.monitoring.soa.Appli;
import com.openwide.easysoa.monitoring.soa.Node;
import com.openwide.easysoa.monitoring.soa.Service;
import com.openwide.easysoa.nuxeo.registration.NuxeoRegistrationService;

/**
 * 
 * @author jguillemotte
 *
 */
public class NodeListener implements UpdateListener {

	/**
	 * Logger
	 */
	static Logger logger = Logger.getLogger(NodeListener.class.getName());
	
	@Override
	public void update(EventBean[] newEvents, EventBean[] oldEvents) {
		logger.debug("***************************************************************");
		logger.debug("receiving event !");	
		if(newEvents != null){
			for (EventBean newEvent : newEvents) {
				update(newEvent);
			}
		}
	}

	/**
	 * Update 
	 * @param newData New event data
	 */
	public void update(EventBean newEvent) {
		logger.debug("[NodeListener] --- Event received: " + newEvent.getUnderlying());
		logger.debug("[NodeListener] --- " + newEvent.getUnderlying().getClass().getName());
		NuxeoRegistrationService nrs = new NuxeoRegistrationService();
		//@SuppressWarnings("unchecked")
		/*HashMap<String,Object> hm = (HashMap<String,Object>)(newEvent.getUnderlying());
		BeanEventBean beb = (BeanEventBean)(hm.get("n"));
		Node soaNode = (Node)(beb.getUnderlying());*/
		Node soaNode = (Node)(newEvent.getUnderlying());		
		if(soaNode instanceof Service){
			Service service = (Service) soaNode;
			service.setCallCount(service.getCallCount() + 1);
			nrs.registerRestService(service);
		} else if(soaNode instanceof Api){
			// Nothing to do, no counter to increase for API in Nuxeo model
		} else if(soaNode instanceof Appli){
			// Nothing to do, no counter to increase for Appli in Nuxeo model
		}
	}	
	
}
