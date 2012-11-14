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
 * What it does : if WSDL exchange type, registers its Service.
 * 
 * @obsolete not used anymore
 * @author jguillemotte
 */
public class DiscoveryModeExchangeListener implements UpdateListener {

	/**
	 * Logger
	 */
	static Logger logger = Logger.getLogger(DiscoveryModeExchangeListener.class.getName());
	
	/**
	 * Update
	 */
	@Override
	public void update(EventBean[] newDatas, EventBean[] oldData) {
		for (EventBean newData : newDatas) {
			update(newData);
		}
    }
	 
	/**
	 * Update method for one EventBean
	 * @param newData New event data
	 */
	public void update(EventBean newData) {
		logger.debug("[MessageListener] --- Event received: " + newData.getUnderlying());
		logger.debug("[MessageListener] --- " + newData.getUnderlying().getClass().getName());
        
		@SuppressWarnings("unchecked")
		HashMap<String,Object> hm = (HashMap<String,Object>)(newData.getUnderlying());
		BeanEventBean beb = (BeanEventBean)(hm.get("s"));
		ExchangeRecord record = (ExchangeRecord)(beb.getUnderlying());
		String serviceName = record.getInMessage().getPath();
		if(serviceName.startsWith("/")){
			serviceName = serviceName.substring(1);
		}
		serviceName = serviceName.replace('/', '_');
		// Only for WSDL exchanges
		// soap and rest exchanges are not supported ?
		if(ExchangeType.WSDL.compareTo(record.getExchange().getExchangeType()) == 0){
			Service service = new Service(record.getInMessage().buildCompleteUrl());
			service.setTitle(serviceName); // TODO rather path (obsolete code)
			service.setHttpMethod(record.getInMessage().getMethod()); // GET (since WSDL)
			service.setCallCount(1);
			// TODO missing fields (obsolete code)
			try {
			    new NuxeoRegistrationService().registerWSDLService(service);
	        } catch (Exception e) {
	            logger.error("Failed to register WSDL service", e);
	        }
		}
	}
	
}
