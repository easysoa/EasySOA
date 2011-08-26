package com.openwide.easysoa.run;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import org.apache.log4j.Logger;
import org.osoa.sca.annotations.Reference;
import org.osoa.sca.annotations.Scope;

import com.openwide.easysoa.monitoring.DiscoveryMonitoringService;
import com.openwide.easysoa.monitoring.Message;
import com.openwide.easysoa.monitoring.MonitoringService;

/**
 * A manager for run's
 * Contains a collection of Run's & Methods to manipulate a Run	
 * runs, start() (if not autostart), stop(), listRuns() / getLastRun()..., rerun(Run)
 * In this version, RunManager can only manage ONE active run and several terminated runs !*
 * 
 * @author jguillemotte
 *
 */
@Scope("composite")
public class RunManagerImpl implements RunManager {
	
	/**
	 * Logger
	 */
	private Logger logger = Logger.getLogger(RunManagerImpl.class.getName());	

	/**
	 * 
	 */
	private boolean autoStart = true;	
	
	/**
	 * Reference to monitoring service : only one monitoring service for the runManager 
	 */
	// TODO One monitoring service for each run.
	@Reference
	MonitoringService monitoringService;

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
		logger.debug("Init RunManagerImpl ...");
	}
	
	/**
	 * Set the auto start. The auto start feature create a new <code>Run</code> automatically even if the start method was not called before.
	 * @param autoStart true if a new run should be created automatically when the method getCurrentRun is called, false otherwise
	 */
	public void setAutoStart(boolean autoStart){
		this.autoStart = autoStart;
		//RunManagerImpl.autoStart = autoStart;
	}
	
	/* (non-Javadoc)
	 * @see com.openwide.easysoa.esperpoc.run.RunManager#getCurrentRun()
	 */
	@Override
	public Run getCurrentRun() throws Exception{
		if(currentRun == null && autoStart){
			// TODO add a better unique id
			Date startDate = new Date();
			start("Auto started run - " + startDate);
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
		StringBuffer error = new StringBuffer();
		if(currentRun == null && checkUniqueRunName(runName)){
			currentRun = new Run(runName);
			currentRun.setStartDate(new Date());
		} else {
			error.append("Unable to start a new run. ");
			if(currentRun != null){
				error.append("The run '" + currentRun.getName() + "' is currently started, please stop the current run before to start another one !");
			} else {
				error.append("The name '" + runName + "' is already in the run list, please choose an other name !");
			}
			throw new Exception(error.toString());
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

	/* (non-Javadoc)
	 * @see com.openwide.easysoa.esperpoc.run.RunManager#record()
	 */	
	@Override
	public void record(Message message) {
		// Get the current run and add a message
		logger.debug("Recording message : " + message);
		try{
			this.getCurrentRun().addMessage(message);
			monitoringService.listen(message);
		}
		catch(Exception ex){
			logger.error("Unable to record message !", ex);
		}
	}

	/* (non-Javadoc)
	 * @see com.openwide.easysoa.esperpoc.run.RunManager#getOrderedRunNames()
	 */	
	@Override
	public List<String> getOrderedRunNames() {
		ArrayList<String> runsNameList = new ArrayList<String>();
		Iterator<Run> runIterator = runList.descendingIterator();
		while(runIterator.hasNext()){
			runsNameList.add(runIterator.next().getName());
		}
		return runsNameList;
	}
	
	/* (non-Javadoc)
	 * @see com.openwide.easysoa.esperpoc.run.RunManager#reRun()
	 */
	@Override
	public void reRun(String runName) throws Exception {
		Run run = getRun(runName);
		// TODO remove this and find a way to re-init the monitoringService (especially the tree)
		monitoringService = new DiscoveryMonitoringService();
		// call directly the listen method, with all the run messages as parameters
		// Pb the listen method is in the monitoringService class .... 
		// 2 solutions :
		// 1st : add a reference to monitoring service in runManager. The same Monitoring service is used for all the run in a same run manager
		// 2nd : Each run can have a different run manager, defined when the run is created. We can give a monitoringServiceFactory as reference to the runManager.
		for(Message message : run.getMessageList()){
			logger.debug("Listening message : " + message);
			monitoringService.listen(message);
		}
		monitoringService.registerDetectedServicesToNuxeo();		
	}

	/* (non-Javadoc)
	 * @see com.openwide.easysoa.esperpoc.run.RunManager#deleteRun()
	 */	
	@Override
	public void deleteRun(String runName) throws Exception {
		this.runList.remove(getRun(runName));
	}

	/* (non-Javadoc)
	 * @see com.openwide.easysoa.esperpoc.run.RunManager#getRun()
	 */	
	@Override
	public Run getRun(String runName) throws Exception {
		Iterator<Run> iter = this.runList.iterator();
		while(iter.hasNext()){
			Run run = iter.next();
			if(run.getName().equalsIgnoreCase(runName)){
				return run;
			}
		}
		throw new Exception("There is no run with the name '" +runName + "'");
	}

	/* (non-Javadoc)
	 * @see com.openwide.easysoa.esperpoc.run.RunManager#getMonitoringService()
	 */	
	@Override
	public MonitoringService getMonitoringService() {
		return this.monitoringService;
	}	
	
	/**
	 * Check if the specified runName is unique
	 * @param runName The run name to check
	 * @return true if the run name is unique, false otherwise
	 */
	private boolean checkUniqueRunName(String runName){
		Iterator<Run> iter = this.runList.iterator();
		while(iter.hasNext()){
			Run run = iter.next();
			if(run.getName().equalsIgnoreCase(runName)){
				return false;
			}
		}
		return true;
	}
	
}
