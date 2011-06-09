package com.openwide.easysoa.esperpoc.esper;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.HashMap;

import org.apache.log4j.Logger;

import com.espertech.esper.client.EventBean;
import com.espertech.esper.client.UpdateListener;
import com.openwide.easysoa.esperpoc.NuxeoRegistrationService;
import com.openwide.easysoa.monitoring.soa.Service;
import com.openwide.easysoa.monitoring.soa.WSDLService;


/**
 * Aggregated message listener
 * only able to use aggregated msg props
 * 
 * @author jguillemotte
 *
 */
public class AggregatedMessageListener implements UpdateListener {

	/**
	 * Logger
	 */
	static Logger logger = Logger.getLogger(AggregatedMessageListener.class.getName());
	
	/**
	 * 
	 */
	public void update(EventBean[] newDatas, EventBean[] oldData) {
		for (EventBean newData : newDatas) {
			update(newData);
		}
    }

	public void update(EventBean newData) {
		logger.debug("[MessageListener] --- Event received: " + newData.getUnderlying());
		logger.debug("[MessageListener] --- " + newData.getUnderlying().getClass().getName());
		NuxeoRegistrationService nrs = new NuxeoRegistrationService();
		HashMap<String, Object> aggregatedProps = (HashMap) (newData.getUnderlying());
		/*Iterator<String> iter = hm.keySet().iterator();
		while(iter.hasNext()){
			String key = iter.next();
			System.out.println("Key : " + key);
			System.out.println("Value : " + hm.get(key));
			System.out.println("Clazz value : " + hm.get(key).getClass().getName());
		}*/
		
		// Service construction + send Esper event
		long count = (Long) aggregatedProps.get("count"); // TODO
		String serviceUrl = (String) aggregatedProps.get("url");
		String messageType = (String) aggregatedProps.get("messageType");

		if("WSDl".equals(messageType)){
			WSDLService service;
			URL url;
			try {
				String servicePath = (String) aggregatedProps.get("url");
				if(servicePath.startsWith("/")){
					servicePath = serviceUrl.substring(1);
				}
				servicePath = serviceUrl.replace('/', '_');
				
				url = new URL(serviceUrl);
				service = new WSDLService(url.getHost(), servicePath, serviceUrl, "POST"); // TODO better method in aggregation
				nrs.registerWSDLService(service);
			} catch (MalformedURLException e) {
				logger.error("Bad url", e);
			}
			
		} else {
			// getting parent url
			int lastSlashIndex = serviceUrl.lastIndexOf('/');
			String parentUrl = serviceUrl.substring(0, lastSlashIndex);
			
			Service service = new Service(serviceUrl, parentUrl);
			// TODO also register to nuxeo parent apis if required
			nrs.registerRestService(service);
			// TODO put urlType in msg and handle it here to register also apis and appliimpls to nuxeo
		}
	}
	
}
