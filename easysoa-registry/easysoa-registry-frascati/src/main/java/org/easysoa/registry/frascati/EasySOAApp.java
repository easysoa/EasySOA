package org.easysoa.registry.frascati;

import org.ow2.frascati.FraSCAti;
import org.ow2.frascati.util.FrascatiException;

public interface EasySOAApp {

	/**
	 * Start the EasySOA app
	 */
	public FraSCAti start() throws FrascatiException;

	/**
	 * Stop the EasySOA app
	 */	
	public void stop() throws FrascatiException;
	
	/**
	 * Returns the FraSCAti instance
	 * @return The FraSCAti instance
	 */
	public FraSCAti getFrascati();
	
}
