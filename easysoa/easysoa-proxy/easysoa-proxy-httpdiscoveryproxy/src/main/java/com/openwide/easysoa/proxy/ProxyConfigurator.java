package com.openwide.easysoa.proxy;

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
			PropertyConfigurator.configure(HttpDiscoveryProxy.class.getClassLoader().getResource("log4j.properties"));
			configuratorAlreadyCalled = true;
		}
	}

}
