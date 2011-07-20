package com.openwide.easysoa.esperpoc;

import org.apache.log4j.PropertyConfigurator;

/**
 * Singleton for logger configuration
 * @author jguillemotte
 *
 */
public class ProxyConfigurator {
	
	private static boolean configuratorAlreadyCalled = false;

	/**
	 * Call only one time
	 * Configuration of logger
	 */
	public static void configure(){
		if(!configuratorAlreadyCalled){
			PropertyConfigurator.configure(HttpProxyImpl.class.getClassLoader().getResource("log4j.properties"));
			configuratorAlreadyCalled = true;
		}
	}

}
