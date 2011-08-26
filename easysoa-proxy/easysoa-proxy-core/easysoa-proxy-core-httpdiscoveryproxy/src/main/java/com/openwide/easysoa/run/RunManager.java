package com.openwide.easysoa.run;

import java.util.List;

import com.openwide.easysoa.monitoring.Message;
import com.openwide.easysoa.monitoring.MonitoringService;

public interface RunManager {

	/**
	 * Returns the current run. if there is no current run and autoStart is set to true, 
	 * a new run is automatically started (autostart), otherwise null is returned
	 * @return The current <code>Run</code>
	 */
	public Run getCurrentRun() throws Exception;
	
	/**
	 * Returns the specified run
	 * @param runName The run name to return
	 * @return The run corresponding to the run name
	 * @throws Exception if the run is not found 
	 */
	public Run getRun(String runName) throws Exception;
	
	/**
	 * Delete the specified run
	 * @param runName The run name
	 * @throws Exception If the run cannot be deleted
	 */
	public void deleteRun(String runName) throws Exception;
	
	/**
	 * Starts a new run. A new <code>Run</code> is started only if the current run was stopped before with a call to the stop() method. 
	 * @param runName The name of the run. Must be unique, the name is the id of the run.
	 */
	public void start(String runName) throws Exception;

	/**
	 * Stop the current run
	 */
	public void stop();

	/**
	 * Returns the last run
	 * @return The last <code>Run</code>
	 */
	public Run getLastRun();

	/**
	 * Record a message in the current run
	 * @param message The <code>Message</code> to record
	 */
	public void record(Message message);
	
	/**
	 * Returns the list of all recorded runs in their record order
	 * @return Return the names of all recorded runs
	 */
	public List<String> getOrderedRunNames();
	
	/**
	 * Rerun a the specified run
	 * @param runName The run to rerun
	 */
	public void reRun(String runName) throws Exception;
	
	/**
	 * 
	 * @return
	 */
	public MonitoringService getMonitoringService();
}