package org.easysoa.api;

/**
 * 
 * @author jguillemotte
 *
 */
public class EasySoaApiFactory {

	// Singleton instance
	private static EasySoaApiFactory instance = null;
	//
	private IDiscoveryRest discoveryRest;
	
	/**
	 * Constructor
	 */
	private EasySoaApiFactory() {
		// Nothing to do
	}
	
	/**
	 * Singleton
	 * @return A unique instance of EasySoaApiFactory
	 */
	public static EasySoaApiFactory getInstance() {
		if(instance == null) {
			synchronized(EasySoaApiFactory.class) { 
				instance = new EasySoaApiFactory();
			}
		}
	   return instance;
	}

	/**
	 * 
	 * @return
	 */
	public IDiscoveryRest getNotificationRest() {
		return discoveryRest;
	}

	/**
	 * 
	 * @param notificationRest
	 */
	public void setDiscoveryRest(IDiscoveryRest notificationRest) {
		this.discoveryRest = notificationRest;
	}
	
}
