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
	private INotificationRest notificationRest;
	
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
	public INotificationRest getNotificationRest() {
		return notificationRest;
	}

	/**
	 * 
	 * @param notificationRest
	 */
	public void setNotificationRest(INotificationRest notificationRest) {
		this.notificationRest = notificationRest;
	}
	
}
