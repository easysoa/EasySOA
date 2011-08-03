package com.openwide.easysoa.esperpoc.esper;

import org.apache.log4j.Logger;
import org.osoa.sca.annotations.Scope;

import com.espertech.esper.client.Configuration;
import com.espertech.esper.client.EPAdministrator;
import com.espertech.esper.client.EPRuntime;
import com.espertech.esper.client.EPServiceProvider;
import com.espertech.esper.client.EPServiceProviderManager;
import com.espertech.esper.client.EPStatement;
import com.openwide.easysoa.esperpoc.PropertyManager;
import com.openwide.easysoa.monitoring.Message;
import com.openwide.easysoa.monitoring.apidetector.UrlTreeNodeEvent;

/**
 * 
 * @author jguillemotte
 *
 */
@Scope("composite")
public class EsperEngineSingleton {

	/**
	 * Logger
	 */
	static Logger logger = Logger.getLogger(EsperEngineSingleton.class.getName());
	
	/**
	 * Esper engine singleton
	 */
	private static EsperEngineSingleton esperEngine = null;
	
	/**
	 * Esper Runtime
	 */
	private static EPRuntime cepRT = null;
	
	/**
	 * Esper Administrator
	 */
	private static EPAdministrator cepAdm = null;
	
	/**
	 * Constructor
	 */
	private EsperEngineSingleton(){
		// get current context classloader                                                                                                                                  
		ClassLoader contextClassloader = Thread.currentThread().getContextClassLoader();
		// then alter the classloader
		Thread.currentThread().setContextClassLoader(EsperEngineSingleton.class.getClassLoader());
        try{
    		// Esper configuration
			Configuration cepConfig = new Configuration();
	        // We register Message as objects the engine will have to handle
	        cepConfig.addEventType("Message", Message.class);
	        cepConfig.addEventType("UrlTreeNodeEvent", UrlTreeNodeEvent.class);
	    	EPServiceProvider cep = EPServiceProviderManager.getProvider("myCEPEngine",cepConfig);
	    	cepRT = cep.getEPRuntime();
	    	cepAdm = cep.getEPAdministrator();
			// Add statement & listener
	    	//EPStatement cepStatementMessage = cepAdm.createEPL("select * from Message");
	    	//EPStatement cepStatementMessage = cepAdm.createEPL("select * from pattern[every-distinct(s.completeMessage) s=Message]");
	    	EPStatement cepStatementMessage = cepAdm.createEPL(PropertyManager.getProperty("esper.message.listener.statement"));
	    	cepStatementMessage.addListener(new MessageListener());
	    	/*
	    	// Message counter statement
	    	//EPStatement cepStatementWindowCounter = cepAdm.createEPL("create window countWindow.win:keepall() as select count(*) as count, completeMessage as service from Message group by completeMessage"); 
	    	// Chaque Message est compté et groupé mais trop d'event générés par le MessageCounter ....
	    	//EPStatement cepStatementMessageCounter = cepAdm.createEPL("select count(*) as count, completeMessage as service from Message group by completeMessage output all every 1 minute");
	    	// Même probleme : le counter genere un event pour chaque message recu ...
	    	//EPStatement cepStatementMessageCounter = cepAdm.createEPL("select count(*) as count, completeMessage as service from Message group by completeMessage output every 1 minute");
	    	//EPStatement cepStatementMessageCounter = cepAdm.createEPL("select count(*) as count, completeMessage as serviceName from Message.win:time_batch(1 min) group by completeMessage");
	    	*/
	    	// Generate a MessageCounter event each minute. Even if no new message is received.
	    	/*EPStatement cepStatementMessageCounter = cepAdm.createEPL(PropertyManager.getProperty("esper.message.counter.statement"));
	    	cepStatementMessageCounter.addListener(new MessageCounter());*/

	    	//TODO Problem with this listener : send the message list every 1 minute even if no new message was added
	    	/*
	    	EPStatement cepStatementAggregatedMessageListener = cepAdm.createEPL(PropertyManager.getProperty("esper.message.aggregatedListener.statement"));
	    	cepStatementAggregatedMessageListener.addListener(new AggregatedMessageListener());
	    	*/
        }
        catch(Throwable t){
        	t.printStackTrace();
        }
		// Restore the class loader to its original value after creating Esper client
		Thread.currentThread().setContextClassLoader(contextClassloader);
	}
	
	/**
	 * Returns the Esper Runtime
	 * @return Esper Runtime
	 */
	public static EPRuntime getEsperRuntime(){
		if(cepRT == null || esperEngine == null){
			esperEngine = new EsperEngineSingleton(); 
		}
		return cepRT;
	}
	
	/**
	 * Returns the Esper Runtime
	 * @return Esper Administrator
	 */
	public static EPAdministrator getEsperAdmin(){
		if(cepRT == null || esperEngine == null){
			esperEngine = new EsperEngineSingleton(); 
		}
		return cepAdm;
	}
	
}
