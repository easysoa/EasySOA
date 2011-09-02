package com.openwide.easysoa.esper;

import org.apache.log4j.Logger;
import org.osoa.sca.annotations.Scope;
import com.espertech.esper.client.Configuration;
import com.espertech.esper.client.EPAdministrator;
import com.espertech.esper.client.EPRuntime;
import com.espertech.esper.client.EPServiceProvider;
import com.espertech.esper.client.EPServiceProviderManager;
import com.espertech.esper.client.EPStatement;
import com.openwide.easysoa.monitoring.Message;
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
        cepConfig.addEventType("Message", Message.class);
        cepConfig.addEventType("Node", Node.class);
        //cepConfig.addEventType("UrlTreeNodeEvent", UrlTreeNodeEvent.class);
    	EPServiceProvider cep = EPServiceProviderManager.getProvider("myCEPEngine",cepConfig);
    	
    	// Getting Esper runtime and Esper admin
    	esperRuntime = cep.getEPRuntime();
    	esperAdmin = cep.getEPAdministrator();

    	// Add statement & listener
    	logger.debug("Registering EPL statement :" + PropertyManager.getProperty("esper.message.listener.statement"));
    	EPStatement cepStatementMessage = esperAdmin.createEPL(PropertyManager.getProperty("esper.message.listener.statement"));
    	cepStatementMessage.addListener(new MessageListener());
    	logger.debug("Registering EPL statement :" + PropertyManager.getProperty("esper.node.listener.statement"));
    	EPStatement cepStatementNode = esperAdmin.createEPL(PropertyManager.getProperty("esper.node.listener.statement"));
    	cepStatementNode.addListener(new NodeListener());    	
	}

	@Override
	public void sendEvent(Message message) {
		logger.debug("Sending Message event");
		this.esperRuntime.sendEvent(message);
	}

	@Override
	public void sendEvent(Node soaNode) {
		logger.debug("Sending Node event");
		this.esperRuntime.sendEvent(soaNode);		
	}
	
	
}
