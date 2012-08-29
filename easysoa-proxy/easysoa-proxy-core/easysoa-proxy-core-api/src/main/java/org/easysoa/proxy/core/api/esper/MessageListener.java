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

package org.easysoa.proxy.core.api.esper;

import java.util.HashMap;

import org.apache.log4j.Logger;
import org.easysoa.proxy.core.api.monitoring.soa.Service;
import org.easysoa.proxy.core.api.nuxeo.registration.NuxeoRegistrationService;
import org.easysoa.records.ExchangeRecord;
import org.easysoa.records.Exchange.ExchangeType;
import com.espertech.esper.client.EventBean;
import com.espertech.esper.client.UpdateListener;
import com.espertech.esper.event.bean.BeanEventBean;

/**
 * Sync message listener
 * able to use all of Message's props, including content
 * 
 * @author jguillemotte
 */
public class MessageListener implements UpdateListener {

	/**
	 * Logger
	 */
	static Logger logger = Logger.getLogger(MessageListener.class.getName());
	
	/**
	 * Update
	 */
	public void update(EventBean[] newDatas, EventBean[] oldData) {
		for (EventBean newData : newDatas) {
			update(newData);
		}
    }
	 
	/**
	 * Update 
	 * @param newData New event data
	 */
	public void update(EventBean newData) {
		logger.debug("[MessageListener] --- Event received: " + newData.getUnderlying());
		logger.debug("[MessageListener] --- " + newData.getUnderlying().getClass().getName());
       
		@SuppressWarnings("unchecked")
		HashMap<String,Object> hm = (HashMap<String,Object>)(newData.getUnderlying());
		BeanEventBean beb = (BeanEventBean)(hm.get("s"));
		//Message msg = (Message)(beb.getUnderlying());
		ExchangeRecord record = (ExchangeRecord)(beb.getUnderlying());
		String serviceName = record.getInMessage().getPath();
		//String serviceName = msg.getPathName();
		if(serviceName.startsWith("/")){
			serviceName = serviceName.substring(1);
		}
		serviceName = serviceName.replace('/', '_');
		//if(MessageType.WSDL.compareTo(msg.getType()) == 0){
		if(ExchangeType.WSDL.compareTo(record.getExchange().getExchangeType()) == 0){
			Service service;
			//WSDLService service = new WSDLService(msg.getHost(), serviceName, msg.getCompleteMessage(), msg.getMethod());
			//service = new Service(msg.getUrl());
			service = new Service(record.getInMessage().buildCompleteUrl());
			service.setTitle(serviceName);
			//service.setHttpMethod(msg.getMethod());
			service.setHttpMethod(record.getInMessage().getMethod());
			service.setCallCount(1);
			try {
			    new NuxeoRegistrationService().registerWSDLService(service);
	        } catch (Exception e) {
	            logger.error("Failed to register WSDL service", e);
	        }
		}
		/*else {
			//List<Node> soaNodes = DiscoveryMonitoringService.getMonitorService().getModel().getSoaNodes();
			Node soaNode = null;
			for(Node node : this.soaNodes){
				if(node.getUrl().equals(msg.getUrl())){
					soaNode = node;
					logger.debug("Node found ! " + soaNode.getTitle());
					break;
				}
			}
			if(soaNode instanceof Service){
				Service service = (Service) soaNode;
				service.setCallCount(service.getCallCount() + 1);
				nrs.registerRestService(service);
			} else if(soaNode instanceof Api){
				// Nothing to do, no counter to increase for API
			} else if(soaNode instanceof Appli){
				// Nothing to do, no counter to increase for Appli
			}
		}*/
	}
	
}
