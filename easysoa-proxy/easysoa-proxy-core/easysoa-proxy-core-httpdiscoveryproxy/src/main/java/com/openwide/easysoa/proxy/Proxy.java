package com.openwide.easysoa.proxy;

/**
 * Proxy interface. SLA Messaging interface.
 * @author jguillemotte
 *
 */
public interface Proxy {
		
	/**
	 * preForward (ex. SLA)
	 * 
	 */
	public void preForward();
	//postForward (ex. async)
	public void postForward();
	//preReturn (ex. translate)
	public void preReturn();
	//postReturn (ex. record log including return OK)
	public void postReturn();

	//handle errors and aborts (ex. SLA).
	public void handle(); 
	
}
