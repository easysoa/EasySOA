package com.openwide.easysoa.esperpoc.run;

import java.util.ArrayDeque;
import java.util.Date;
import java.util.List;
import org.apache.log4j.Logger;

/**
 * A manager for run's
 * Contains a collection of Run's & Methods to manipulate a Run	
 * runs, start() (if not autostart), stop(), listRuns() / getLastRun()..., rerun(Run)
 * In this version, RunManager can only manage ONE active run and several terminated runs !*
 * 
 * @author jguillemotte
 *
 */
public class RunManagerImpl implements RunManager {
	
	/**
	 * Logger
	 */
	private static Logger logger = Logger.getLogger(RunManagerImpl.class.getName());	

	/**
	 * 
	 */
	private static boolean autoStart = true;
	
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

	/**
	 * 
	 */
	public RunManagerImpl(){
		runList = new ArrayDeque<Run>();
	}
	
	/**
	 * Return an instance of RunManager
	 * @return An instance of <code>RunManager</code>
	 */
	public static RunManager getInstance(){
		if(runManager == null){
			runManager = new RunManagerImpl();
		}
		return runManager;
	}
	
	/**
	 * Set the auto start. The auto start feature create a new <code>Run</code> automatically even if the start method was not called before.
	 * @param autoStart true if a new run should be created automatically when the method getCurrentRun is called, false otherwise
	 */
	public static void setAutoStart(boolean autoStart){
		RunManagerImpl.autoStart = autoStart;
	}
	
	/* (non-Javadoc)
	 * @see com.openwide.easysoa.esperpoc.run.RunManager#getCurrentRun()
	 */
	@Override
	public Run getCurrentRun() throws Exception{
		if(currentRun == null && autoStart){
			try {
				start("Auto started run");
			} catch (Exception e) {
				// Nothing to do here
			}
		} else if(currentRun==null && !autoStart){
			throw new Exception("Autostart is set to false, unable to start a new run automatically !");
		}
		
		return this.currentRun;
	}
	
	/* (non-Javadoc)
	 * @see com.openwide.easysoa.esperpoc.run.RunManager#start(java.lang.String)
	 */
	@Override
	public void start(String runName) throws Exception {
		if(currentRun == null){
			currentRun = new Run(runName);
			currentRun.setStartDate(new Date());
		} else {
			throw new Exception("The run '" + currentRun.getName() + "' is currently started, please stop the current run before to start another one !");
		}
	}
	
	/* (non-Javadoc)
	 * @see com.openwide.easysoa.esperpoc.run.RunManager#stop()
	 */
	@Override
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
	
	/* (non-Javadoc)
	 * @see com.openwide.easysoa.esperpoc.run.RunManager#getLastRun()
	 */
	@Override
	public Run getLastRun(){
		return this.runList.getLast();
	}
	
	/**
	 * 
	 * @param run
	 */
	/*public void reRun(Run run){
		
	}*/
	
}
