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

package com.openwide.easysoa.esper;

import org.apache.log4j.Logger;
import org.easysoa.records.ExchangeRecord;
import org.osoa.sca.annotations.Scope;
import com.espertech.esper.client.Configuration;
import com.espertech.esper.client.EPAdministrator;
import com.espertech.esper.client.EPRuntime;
import com.espertech.esper.client.EPServiceProvider;
import com.espertech.esper.client.EPServiceProviderManager;
import com.espertech.esper.client.EPStatement;
//import com.openwide.easysoa.monitoring.Message;
import com.openwide.easysoa.monitoring.soa.Node;
import com.openwide.easysoa.proxy.PropertyManager;

/**
 * Esper engine
 * @author jguillemotte
 *
 */
@Scope("composite")
public class EsperEngineImpl implements EsperEngine {

	/**
	 * Logger
	 */
	private static Logger logger = Logger.getLogger(EsperEngineImpl.class.getName());	
	
	// Esper Runtime
	private EPRuntime esperRuntime;;
	
	// Esper Administrator
	private EPAdministrator esperAdmin;;
	
	/**
	 * Initialize the engine, registering listeners and EPL statements 
	 */
	public EsperEngineImpl(){
		// Esper configuration
		Configuration cepConfig = new Configuration();
        
		// Registering Message and Node as objects the engine will have to handle
        //cepConfig.addEventType("Message", Message.class);
		cepConfig.addEventType("ExchangeRecord", ExchangeRecord.class);
        cepConfig.addEventType("Node", Node.class);
        //cepConfig.addEventType("UrlTreeNodeEvent", UrlTreeNodeEvent.class);
    	EPServiceProvider cep = EPServiceProviderManager.getProvider("myCEPEngine",cepConfig);
    	
    	// Getting Esper runtime and Esper admin
    	esperRuntime = cep.getEPRuntime();
    	esperAdmin = cep.getEPAdministrator();

    	// Add statement & listener
    	//logger.debug("Registering EPL statement :" + PropertyManager.getProperty("esper.message.listener.statement"));
    	//EPStatement cepStatementMessage = esperAdmin.createEPL(PropertyManager.getProperty("esper.message.listener.statement"));
    	logger.debug("Registering EPL statement :" + PropertyManager.getProperty("esper.exchange.listener.statement"));
    	EPStatement cepStatementMessage = esperAdmin.createEPL(PropertyManager.getProperty("esper.exchange.listener.statement"));    	
    	cepStatementMessage.addListener(new MessageListener());
    	logger.debug("Registering EPL statement :" + PropertyManager.getProperty("esper.node.listener.statement"));
    	EPStatement cepStatementNode = esperAdmin.createEPL(PropertyManager.getProperty("esper.node.listener.statement"));
    	cepStatementNode.addListener(new NodeListener());    	
	}

	@Override
	//public void sendEvent(Message message) {
	public void sendEvent(ExchangeRecord exchangeRecord) {
		logger.debug("Sending Exchange record event");
		this.esperRuntime.sendEvent(exchangeRecord);
	}

	@Override
	public void sendEvent(Node soaNode) {
		logger.debug("Sending Node event");
		this.esperRuntime.sendEvent(soaNode);		
	}
	
	
}
