package com.openwide.easysoa.esperpoc.run;

import java.util.List;

import com.openwide.easysoa.monitoring.Message;

public interface RunManager {

	/**
	 * Returns the current run. if there is no current run and autoStart is set to true, 
	 * a new run is automatically started (autostart), otherwise null is returned
	 * @return The current <code>Run</code>
	 */
	public abstract Run getCurrentRun() throws Exception;

	/**
	 * Starts a new run. A new <code>Run</code> is started only if the current run was stopped before with a call to the stop() method. 
	 * @param runName The name of the run
	 */
	public abstract void start(String runName) throws Exception;

	/**
	 * Stop the current run
	 */
	public abstract void stop();

	/**
	 * Returns the last run
	 * @return The last <code>Run</code>
	 */
	public abstract Run getLastRun();

	/**
	 * Record a message in the current run
	 * @param message The <code>Message</code> to record
	 */
	public abstract void record(Message message);
	
	/**
	 * Returns the list of all recorded runs in their record order
	 * @return Return the names of all recorded runs
	 */
	public abstract List<String> getOrderedRunNames();
	
}