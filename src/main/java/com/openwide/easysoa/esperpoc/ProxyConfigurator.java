package com.openwide.easysoa.esperpoc;

import org.apache.log4j.PropertyConfigurator;

public class ProxyConfigurator {
	
	private static boolean configuratorAlreadyCalled = false;

	/**
	 * Call only one time
	 */
	public static void configure(){
		if(!configuratorAlreadyCalled){
			PropertyConfigurator.configure(HttpProxyImpl.class.getClassLoader().getResource("log4j.properties"));
			configuratorAlreadyCalled = true;
		}
	}

}
