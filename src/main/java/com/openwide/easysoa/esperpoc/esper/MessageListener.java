package com.openwide.easysoa.esperpoc.esper;

import java.util.HashMap;
import org.apache.log4j.Logger;
//import java.util.Iterator;
import com.espertech.esper.client.EventBean;
import com.espertech.esper.client.UpdateListener;
import com.espertech.esper.event.bean.BeanEventBean;
import com.openwide.easysoa.esperpoc.NuxeoRegistrationService;

public class MessageListener implements UpdateListener {

	/**
	 * Logger
	 */
	static Logger logger = Logger.getLogger(MessageListener.class.getName());
	
	public void update(EventBean[] newData, EventBean[] oldData) {
		logger.debug("[MessageListener] --- Event received: " + newData[0].getUnderlying());
		logger.debug("[MessageListener] --- " + newData[0].getUnderlying().getClass().getName());
		//Message msg = (Message)(newData[0].getUnderlying());
		@SuppressWarnings("unchecked")
		HashMap<String,Object> hm = (HashMap<String,Object>)(newData[0].getUnderlying());
		/*Iterator<String> iter = hm.keySet().iterator();
		while(iter.hasNext()){
			String key = iter.next();
			System.out.println("Key : " + key);
			System.out.println("Value : " + hm.get(key));
			System.out.println("Clazz value : " + hm.get(key).getClass().getName());
		}*/
		//Message msg =  (Message)(hm.get("s"));
		BeanEventBean beb = (BeanEventBean)(hm.get("s"));
		Message msg = (Message)(beb.getUnderlying());
		
		// Construction d'un service + send service event
		// TODO STATIC variable for Nuxeo address
		String serviceName = msg.getPathName();
		if(serviceName.startsWith("/")){
			serviceName = serviceName.substring(1);
		}
		serviceName = serviceName.replace('/', '_');
		Service service = new Service(msg.getHost(), serviceName, msg.getCompleteMessage());
		NuxeoRegistrationService nrs = new NuxeoRegistrationService();
		nrs.registerService(service);
    }
	
}
