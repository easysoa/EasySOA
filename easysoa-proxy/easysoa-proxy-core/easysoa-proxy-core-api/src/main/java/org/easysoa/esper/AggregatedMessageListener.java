/**
 * EasySOA Proxy
 * Copyright 2011 Open Wide
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * 
 * Contact : easysoa-dev@googlegroups.com
 */

package org.easysoa.esper;

import java.util.HashMap;

import org.apache.log4j.Logger;

import com.espertech.esper.client.EventBean;
import com.espertech.esper.client.UpdateListener;

/**
 * Aggregated message listener
 * only able to use aggregated msg props
 * 
 * 
 * @author jguillemotte
 *
 */
@Deprecated
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

	/**
	 * 
	 * @param newData
	 */
	public void update(EventBean newData) {
		logger.debug("[AggregatedMessageListener] --- Event received: " + newData.getUnderlying());
		logger.debug("[AggregatedMessageListener] --- " + newData.getUnderlying().getClass().getName());
		//NuxeoRegistrationService nrs = new NuxeoRegistrationService();
		
        @SuppressWarnings("unchecked")
        HashMap<String, Object> aggregatedProps = (HashMap<String, Object>) (newData.getUnderlying());
		for(String property : aggregatedProps.keySet()){
			logger.debug("Property : " + property);
			logger.debug("Value : " + aggregatedProps.get(property));
			logger.debug("Clazz value : " + aggregatedProps.get(property).getClass().getName());			
		}
		/*
		Iterator<String> iter = aggregatedProps.keySet().iterator();
		while(iter.hasNext()){
			String key = iter.next();
			logger.debug("Key : " + key);
			logger.debug("Value : " + aggregatedProps.get(key));
			logger.debug("Clazz value : " + aggregatedProps.get(key).getClass().getName());
		}*/
		/*		
		BeanEventBean beb = (BeanEventBean)(aggregatedProps.get("s"));
		Message msg = (Message)(beb.getUnderlying());		
		// Service construction + send Esper event
		long count = (Long) aggregatedProps.get("count");
		String serviceUrl = (String) aggregatedProps.get("url");
		MessageType messageType = (MessageType) aggregatedProps.get("messageType");
		 */
		//if(MessageType.WSDL.compareTo(messageType) == 0){
		/*	WSDLService service;
			URL url;*/
			/*try {
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
			}*/
			
		//} else {
			// getting parent url
			//int lastSlashIndex = msg.getUrl().lastIndexOf('/');
			//String parentUrl = msg.getUrl().substring(0, lastSlashIndex);
			/*
			List<Node> soaNodes = MonitorService.getMonitorService().getModel().getSoaNodes();
			Node soaNode = null;
			for(Node node : soaNodes){
				if(node.getUrl().equals(msg.getUrl())){
					soaNode = node;
					logger.debug("Node found ! " + soaNode.getTitle());					
				}
			}
			Service service;
			if(soaNode instanceof Service){
				service = (Service) soaNode;
				service.setCallCount(service.getCallCount() + 1);
			} else {
				service = new Service(msg.getUrl(), parentUrl);
				service.setTitle(msg.getUrl().substring(lastSlashIndex+1));
				service.setDescription(msg.getUrl().substring(lastSlashIndex+1));
				service.setCallCount(1);
			}
			// TODO also register to nuxeo parent apis if required
			nrs.registerRestService(service);
			// TODO put urlType in msg and handle it here to register also apis and appliimpls to nuxeo
			*/
		//}
	}
	
}
