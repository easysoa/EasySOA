package com.openwide.easysoa.esperpoc;

import java.util.ArrayDeque;
import java.util.Date;
import java.util.List;
import org.apache.log4j.Logger;

public class RunManager {
	
	/**
	 * Logger
	 */
	private static Logger logger = Logger.getLogger(RunManager.class.getName());	

	/**
	 * 
	 */
	private static RunManager runManager = null;

	/**
	 * The current run
	 */
	private Run currentRun;
	
	/**
	 * Run list
	 */
	private ArrayDeque<Run> runList;
	
	// Contains a collection of Run's & Methods to manipulate a Run	
	// runs, start() (if not autostart), stop(), listRuns() / getLastRun()..., rerun(Run)
	// In this version, RunManager can only manage ONE active run and several terminated runs !
	
	private RunManager(){
		runList = new ArrayDeque<Run>();
	}
	
	/**
	 * Return an instance of RunManager
	 * @return An instance of <code>RunManager</code>
	 */
	public static RunManager getInstance(){
		if(runManager == null){
			runManager = new RunManager();
		}
		return runManager;
	}
	
	/**
	 * Returns the current run. if thers is no current run, a new run is automatically started (autostart).
	 * @return The current <code>Run</code>
	 */
	public Run getCurrentRun(){
		if(currentRun == null){
			start("Auto started run");
		}
		return this.currentRun;
	}
	
	/**
	 * Starts a new run. A new <code>Run</code> is started only if the current run was stopped before with a call to the stop() method. 
	 */
	//TODO if the current run is not stopped, throw a new exception !!
	public void start(String runName){
		if(currentRun == null){
			currentRun = new Run(runName);
			currentRun.setStartDate(new Date());
		}
	}
	
	/**
	 * Stop the current run
	 */
	public void stop(){
		if(this.currentRun != null){
			this.currentRun.setStopDate(new Date());
			this.runList.add(currentRun);
			this.currentRun = null;
		}
	}
	
	/**
	 * Returns the run list
	 * @return A <code>List</code> of <code>Run</code>
	 */
	public List<Run> listRuns(){
		return this.listRuns();
	}
	
	/**
	 * Returns the last run
	 * @return The last <code>Run</code>
	 */
	public Run getLastRun(){
		return this.runList.getLast();
	}
	
	/**
	 * 
	 * @param run
	 */
	//TODO Add code in this method (rerun)
	public void reRun(Run run){
		
	}
	
}
