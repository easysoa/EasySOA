package com.openwide.easysoa.monitoring;

public interface MessageHandler {
	
	/**
	 * return if the message can be handled by this handler
	 * @param message The message to handle 
	 * @return True if the message can be handled, false otherwise
	 */
	public boolean isOkFor(Message message);
	
	/**
	 * Handle the message
	 * @param message The message to handle
	 */
	public void handle(Message message);

}
