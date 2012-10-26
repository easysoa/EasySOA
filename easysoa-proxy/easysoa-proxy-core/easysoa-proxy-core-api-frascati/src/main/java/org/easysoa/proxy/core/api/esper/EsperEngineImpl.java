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

import org.apache.log4j.Logger;
import org.easysoa.proxy.core.api.esper.EsperEngine;
import org.easysoa.proxy.core.api.esper.ExchangeListener;
import org.easysoa.proxy.core.api.esper.NodeListener;
import org.easysoa.proxy.core.api.monitoring.soa.Node;
import org.easysoa.proxy.core.api.properties.PropertyManager;
import org.easysoa.records.ExchangeRecord;
import org.osoa.sca.annotations.Scope;
import com.espertech.esper.client.Configuration;
import com.espertech.esper.client.EPAdministrator;
import com.espertech.esper.client.EPRuntime;
import com.espertech.esper.client.EPServiceProvider;
import com.espertech.esper.client.EPServiceProviderManager;
import com.espertech.esper.client.EPStatement;

/**
 * Esper engine implementation
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
	 * @throws Exception  
	 */
	public EsperEngineImpl() throws Exception {
		// Esper configuration
		Configuration cepConfig = new Configuration();
        
		// Registering Exchangerecord and Node as objects the engine will have to handle
		cepConfig.addEventType("ExchangeRecord", ExchangeRecord.class);
        cepConfig.addEventType("Node", Node.class);
    	EPServiceProvider cep = EPServiceProviderManager.getProvider("myCEPEngine",cepConfig);
    	
    	// Getting Esper runtime and Esper admin
    	esperRuntime = cep.getEPRuntime();
    	esperAdmin = cep.getEPAdministrator();

    	// Add statement & listener
    	PropertyManager propertyManager = PropertyManager.getPropertyManager();
    	// Statement for ExchangeRecord objects
    	logger.debug("Registering EPL statement :" + propertyManager.getProperty("esper.exchange.listener.statement"));
    	EPStatement cepStatementExchange = esperAdmin.createEPL(propertyManager.getProperty("esper.exchange.listener.statement"));    	
    	cepStatementExchange.addListener(new ExchangeListener());
        // Statement for Node objects
    	logger.debug("Registering EPL statement :" + propertyManager.getProperty("esper.node.listener.statement"));
    	EPStatement cepStatementNode = esperAdmin.createEPL(propertyManager.getProperty("esper.node.listener.statement"));
    	cepStatementNode.addListener(new NodeListener());    	
	}

	@Override
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
