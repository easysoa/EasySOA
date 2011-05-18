package com.openwide.easysoa.esperpoc;

import com.espertech.esper.client.Configuration;
import com.espertech.esper.client.EPAdministrator;
import com.espertech.esper.client.EPRuntime;
import com.espertech.esper.client.EPServiceProvider;
import com.espertech.esper.client.EPServiceProviderManager;
import com.espertech.esper.client.EPStatement;
import com.openwide.easysoa.esperpoc.esper.Message;
import com.openwide.easysoa.esperpoc.esper.MessageListener;

public class EsperEngineSingleton {

	/**
	 * Esper engine singleton
	 */
	private static EsperEngineSingleton esperEngine = null;
	
	/**
	 * Esper Runtime
	 */
	private static EPRuntime cepRT;
	
	/**
	 * Constructor
	 */
	private EsperEngineSingleton(){
		// get current context classloader                                                                                                                                  
		ClassLoader contextClassloader = Thread.currentThread().getContextClassLoader();
		// then alter the class-loader
		Thread.currentThread().setContextClassLoader(EsperEngineSingleton.class.getClassLoader());
        try{
    		// Esper configuration
			Configuration cepConfig = new Configuration();
	        // We register Message as objects the engine will have to handle
	        cepConfig.addEventType("Message", Message.class);	
	    	EPServiceProvider cep = EPServiceProviderManager.getProvider("myCEPEngine",cepConfig);
	    	cepRT = cep.getEPRuntime();
	    	EPAdministrator cepAdm = cep.getEPAdministrator();
			// Add statement & listener
	    	//EPStatement cepStatementMessage = cepAdm.createEPL("select * from Message");
	    	EPStatement cepStatementMessage = cepAdm.createEPL("select * from pattern[every-distinct(s.completeMessage) s=Message]"); 
	    	cepStatementMessage.addListener(new MessageListener());
	    	// Message counter statement
	    	//EPStatement cepStatementMessageCounter = cepAdm.createEPL("select count(*) from Message group by completeMessage"); 
	    	//cepStatementMessageCounter.addListener(new MessageCounter());
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
	
}
