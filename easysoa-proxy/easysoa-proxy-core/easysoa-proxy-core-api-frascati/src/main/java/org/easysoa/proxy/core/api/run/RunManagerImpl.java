/**
 * EasySOA Proxy
 * Copyright 2011 Open Wide
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * 
 * Contact : easysoa-dev@googlegroups.com
 */

package org.easysoa.proxy.core.api.run;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.apache.log4j.Logger;
import org.easysoa.proxy.core.api.messages.server.NumberGenerator;
import org.easysoa.proxy.core.api.records.persistence.filesystem.ProxyFileStore;
//import org.easysoa.proxy.core.api.records.replay.ReplayEngine;
import org.easysoa.proxy.core.api.run.Run;
import org.easysoa.proxy.core.api.run.RunManager;
import org.easysoa.proxy.core.api.run.RunManagerEventReceiver;
import org.easysoa.proxy.core.api.run.Run.RunStatus;
import org.easysoa.records.ExchangeRecord;
/*
import org.easysoa.records.filters.ExchangeRecordServletFilter;
import org.easysoa.records.handlers.NuxeoMessageExchangeRecordHandler;
import org.easysoa.records.service.ExchangeRecordServletFilterService;
import org.easysoa.records.service.ExchangeRecordServletFilterServiceImpl;
*/
import org.osoa.sca.annotations.ConversationID;
import org.osoa.sca.annotations.Reference;
import org.osoa.sca.annotations.Scope;

/**
 * A manager for run's
 * Contains a collection of Run's & Methods to manipulate a Run	
 * runs, start() (if not autostart), stop(), listRuns() / getLastRun()..., rerun(Run)
 * In this version, RunManager can only manage ONE active run and several terminated runs !*
 * 
 * LATER manage several currentRuns, and synchronize on them rather than on the RunManagerImpl instance.
 * 
 * @author jguillemotte
 *
 */
// TODO Replace composite annotation by conversation or .....
@Scope("composite")
public class RunManagerImpl implements RunManager {

    // TODO : Update the checkUniqueRunName method !
    // one different run manager for each proxy ... We need to avoid duplicate run names ....
    // How to give a name to the current name ??
    // - Client must be give id used as a prefix or suffix ?

	/**
	 * Logger
	 */
	private Logger logger = Logger.getLogger(RunManagerImpl.class.getName());	
	
	/**
	 * when set to true, a run is automatically started when the getCurrentRun is called if there is no current run.
	 */
	private boolean autoStart = false;
	
	/**
	 * When set to true, the run is automatically saved when the stop method is called. 
	 */
	private boolean autoSave = true;

	// If conversation scope => a different number generator for each
	// instance of run manager
	@Reference
	NumberGenerator exchangeNumberGenerator;
	
	//@Reference
	//ReplayEngine replayEngine;
	
	/**
	 * List of event receivers
	 */
	private List<RunManagerEventReceiver> runManagerEventReceiverList = new ArrayList<RunManagerEventReceiver>();

	/**
	 * Run store
	 */
    private ProxyFileStore erStore = new ProxyFileStore();
    
	/**
	 * The current run
	 */
	private Run currentRun;
	
	/**
	 * 
	 */
	public RunManagerImpl(){
		logger.debug("Init RunManagerImpl ...");
	}
	
	/**
	 * Register a new event receiver
	 * @param eventReceiver The RunManagerEventReceiver to register
	 */
	public void addEventReceiver(RunManagerEventReceiver eventReceiver){
	    if(eventReceiver != null){
	        runManagerEventReceiverList.add(eventReceiver);
	    }
	}
	
	/* (non-Javadoc)
	 * @see org.easysoa.esperpoc.run.RunManager#getCurrentRun()
	 */
	@Override
	public Run getCurrentRun() throws Exception {
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
	 * @see org.easysoa.esperpoc.run.RunManager#start(java.lang.String)
	 */
	@Override
	public String start(String runName) throws Exception {
	    
	    /*try{
            ExchangeRecordServletFilterService filterService = new ExchangeRecordServletFilterServiceImpl();
            ExchangeRecordServletFilter filter = filterService.getExchangeRecordServletFilter();
	        filter.start(new NuxeoMessageExchangeRecordHandler());
	    } catch(Exception ex){
	        logger.error("Unable to call start method of ExchangeRecordServletFilter", ex);
	    }*/
	    
		StringBuffer error = new StringBuffer();
        synchronized (RunManagerImpl.this) { // forbid simultaneous record, start, stop, save, delete
    		if(currentRun == null && checkUniqueRunName(runName)){
    			currentRun = new Run(runName);
    			currentRun.setStartDate(new Date());
    			currentRun.setStatus(RunStatus.RUNNING);
    		    for(RunManagerEventReceiver eventReceiver : runManagerEventReceiverList){
    		        try {
    		            eventReceiver.receiveEvent(RunManagerEvent.START);
    			    }
    		        catch(Exception ex) {
    		            logger.error("Cannot send event to event receiver : " + eventReceiver.getEventReceiverName(), ex);
    		        }
                }
                return "Run " + runName + " started !";
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
	}
	
	/* (non-Javadoc)
	 * @see org.easysoa.esperpoc.run.RunManager#stop()
	 */
	@Override
	public String stop() throws Exception {
        synchronized (RunManagerImpl.this) { // forbid simultaneous record, start, stop, save, delete
            if(this.currentRun != null){
                String response = "Run " + currentRun.getName() + " stopped and deleted !";
    			this.currentRun.setStopDate(new Date());
    			this.currentRun.setStatus(RunStatus.STOPPED);
    			if (autoSave) {
    				//internalSave();
    			    erStore.save(currentRun);
    		        currentRun.setStatus(RunStatus.SAVED);
    		        response = "Run " + currentRun.getName() + " stopped, saved and deleted !";
    			}
                this.currentRun = null;
                for(RunManagerEventReceiver eventReceiver : runManagerEventReceiverList){
                    try {
                        eventReceiver.receiveEvent(RunManagerEvent.STOP);
                    }
                    catch(Exception ex) {
                        logger.error("Cannot send event to event receiver : " + eventReceiver.getEventReceiverName(), ex);
                    }
                }
                return response;
            } else {
                throw new Exception("There is no current run to stop !");
            }
        }
	}
	
	/**
	 * Returns the run list
	 * @return A <code>List</code> of <code>Run</code>
	 */
	/*public ArrayDeque<Run> listRuns(){
		return this.runList;
	}*/
	
	/* (non-Javadoc)
	 * @see org.easysoa.esperpoc.run.RunManager#getLastRun()
	 */
	/*@Override
	public Run getLastRun(){
		return this.runList.getLast();
	}*/

	/* (non-Javadoc)
	 * @see org.easysoa.esperpoc.run.RunManager#record()
	 */	
	@Override
	public void record(ExchangeRecord exchangeRecord){
        synchronized (RunManagerImpl.this) { // forbid simultaneous record, start, stop, save, delete
            if(this.currentRun != null){
        		// Get the current run and add a message
        		logger.debug("Recording message : " + exchangeRecord);
        		try{
        		    exchangeRecord.getExchange().setExchangeID(Long.toString(exchangeNumberGenerator.getNextNumber()));
    	            this.getCurrentRun().addExchange(exchangeRecord);
        		}
        		catch(Exception ex){
        			logger.error("Unable to record message !", ex);
        		}    
            } else {
                logger.error("Unable to record message : There is no current run !");
            }
        }
	}
	
	/* (non-Javadoc)
	 * @see org.easysoa.esperpoc.run.RunManager#reRun()
	 */
	/*@Override
	public void reRun(String runName) throws Exception {
		Run run = getRun(runName);
		// TODO remove this and find a way to re-init the monitoringService (especially the tree)
		monitoringService = new DiscoveryMonitoringService();
		// call directly the listen method, with all the run messages as parameters
		// Pb the listen method is in the monitoringService class .... 
		// 2 solutions :
		// 1st : add a reference to monitoring service in runManager. The same Monitoring service is used for all the run in a same run manager
		// 2nd : Each run can have a different run manager, defined when the run is created. We can give a monitoringServiceFactory as reference to the runManager.
		//for(Message message : run.getMessageList()){
		//	logger.debug("Listening message : " + message);
		//	monitoringService.listen(message);
		}
		for(ExchangeRecord exchangeRecord : run.getExchangeRecordList()){
			logger.debug("Listening exchange record : " + exchangeRecord);
			monitoringService.listen(exchangeRecord);
		}
		monitoringService.registerDetectedServicesToNuxeo();		
	}*/

	/* (non-Javadoc)
	 * @see org.easysoa.esperpoc.run.RunManager#deleteRun()
	 */	
	@Override
	public String delete() throws Exception {
        synchronized (RunManagerImpl.this) { // forbid simultaneous record, start, stop, save, delete
            if(this.currentRun != null){
                currentRun = null;
                return "Run deleted !";
            } else {
                throw new Exception("There is no current run to delete !");
            }
        }
	}

	/* (non-Javadoc)
	 * @see org.easysoa.esperpoc.run.RunManager#getRun()
	 */	
	/*@Override
	public Run getRun(String runName) throws Exception {
		Iterator<Run> iter = this.runList.iterator();
		while(iter.hasNext()){
			Run run = iter.next();
			if(run.getName().equalsIgnoreCase(runName)){
				return run;
			}
		}
		throw new Exception("There is no run with the name '" +runName + "'");
	}*/

	@Override
	public String save() throws Exception {
        synchronized (RunManagerImpl.this) { // forbid simultaneous record, start, stop, save, delete
            if(this.currentRun != null){
                erStore.save(currentRun);
                currentRun.setStatus(RunStatus.SAVED);
                return "Run " + currentRun.getName() + " saved !";
            } else {
                throw new Exception("There is no current run to save !");
            }
	    }
	}
	
	/**
	 * Check if the specified runName is unique
	 * @param runName The run name to check
	 * @return true if the run name is unique, false otherwise
	 */
	private boolean checkUniqueRunName(String runName){
		// TODO rewrite this method to check the run folder names
	    
	    // problem with conversation scope => several run manager at the same time .....
	    // To check
	    // - previous store with the same name already exists in store folder
	    // - A run with the same name already exists in another run manager but is not already saved ...
	    
	    // Solution => use a RunNameChecker as a singleton in all run manager instances
	    
		/*Iterator<Run> iter = this.runList.iterator();
		while(iter.hasNext()){
			Run run = iter.next();
			if(run.getName().equalsIgnoreCase(runName)){
				return false;
			}
		}*/
		return true;
	}

	@Override
	public boolean isCurrentRun() {
		if(currentRun != null){
			return true;
		} else {
			return false;			
		}
	}

    /**
     * Set the auto start. The auto start feature create a new <code>Run</code> automatically even if the start method was not called before.
     * @param autoStart true if a new run should be created automatically when the method getCurrentRun is called, false otherwise
     */
    public void setAutoStart(boolean autoStart){
        this.autoStart = autoStart;
    }

    /**
     * Returns true id the auto start is enabled
     * @return true id the auto start is enabled 
     */
    public boolean isAutoStart() {
        return autoStart;
    }   

    /**
     * Set the auto save. The auto save feature save automatically the run when the stop method is called.
     * @param autoStart true if the current run should be saved automatically when the method stop is called, false otherwise
     */
    public void setAutoSave(boolean autoSave) {
        this.autoSave = autoSave;
    }

    /**
     * Returns true id the auto save is enabled
     * @return true id the auto save is enabled 
     */ 
    public boolean isAutoSave() {
        return autoSave;
    }
    
}
