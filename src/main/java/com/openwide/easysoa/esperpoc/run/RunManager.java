package com.openwide.easysoa.esperpoc.run;

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

}