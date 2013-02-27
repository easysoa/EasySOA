/**
 * EasySOA Proxy Copyright 2011 Open Wide
 *
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
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
import org.easysoa.proxy.core.api.run.Run.RunStatus;
import org.easysoa.records.ExchangeRecord;
import org.osoa.sca.annotations.Reference;
import org.osoa.sca.annotations.Scope;

/**
 * A manager for run's Contains a collection of Run's & Methods to manipulate a
 * Run runs, start() (if not autostart), stop(), listRuns() / getLastRun()...,
 * rerun(Run) In this version, RunManager can only manage ONE active run and
 * several terminated runs !*
 *
 * LATER manage several currentRuns, and synchronize on them rather than on the
 * RunManagerImpl instance.
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
     * when set to true, a run is automatically started when the getCurrentRun
     * is called if there is no current run.
     */
    private boolean autoStart = false;

    /**
     * When set to true, the run is automatically saved when the stop method is
     * called.
     */
    private boolean autoSave = true;
    // If conversation scope => a different number generator for each
    // instance of run manager
    @Reference
    NumberGenerator exchangeNumberGenerator;
    
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
    public RunManagerImpl() {
        logger.debug("Init RunManagerImpl ...");
    }

    /**
     * Register a new event receiver
     *
     * @param eventReceiver The RunManagerEventReceiver to register
     */
    @Override
    public void addEventReceiver(RunManagerEventReceiver eventReceiver) {
        if (eventReceiver != null) {
            runManagerEventReceiverList.add(eventReceiver);
        }
    }

    /* (non-Javadoc)
     * @see org.easysoa.esperpoc.run.RunManager#getCurrentRun()
     */
    @Override
    public Run getCurrentRun() throws Exception {
        if (currentRun == null && autoStart) {
            // TODO add a better unique id
            Date startDate = new Date();
            start("Auto started run - " + startDate);
        } else if (currentRun == null && !autoStart) {
            throw new Exception("Autostart is set to false, unable to start a new run automatically !");
        }
        return this.currentRun;
    }

    /* (non-Javadoc)
     * @see org.easysoa.esperpoc.run.RunManager#start(java.lang.String)
     */
    @Override
    public String start(String runName) throws Exception {

        StringBuilder error = new StringBuilder();
        synchronized (RunManagerImpl.this) { // forbid simultaneous record, start, stop, save, delete
            if (this.currentRun == null && checkUniqueRunName(runName)) {
                this.currentRun = new Run(runName);
                this.currentRun.setStartDate(new Date());
                this.currentRun.setStatus(RunStatus.RUNNING);
                for (RunManagerEventReceiver eventReceiver : runManagerEventReceiverList) {
                    try {
                        eventReceiver.receiveEvent(RunManagerEvent.START);
                    } catch (Exception ex) {
                        logger.error("Cannot send event to event receiver : " + eventReceiver.getEventReceiverName(), ex);
                    }
                }
                return "Run " + this.currentRun.getName() + " started !";
            } else {
                error.append("Unable to start a new run. ");
                if (currentRun != null) {
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
            if (this.currentRun != null) {
                String response = "Run " + currentRun.getName() + " stopped and deleted !";
                this.currentRun.setStopDate(new Date());
                this.currentRun.setStatus(RunStatus.STOPPED);
                if (autoSave) {
                    erStore.save(currentRun);
                    currentRun.setStatus(RunStatus.SAVED);
                    response = "Run " + currentRun.getName() + " stopped, saved and deleted !";
                }
                this.currentRun = null;
                for (RunManagerEventReceiver eventReceiver : runManagerEventReceiverList) {
                    try {
                        eventReceiver.receiveEvent(RunManagerEvent.STOP);
                    } catch (Exception ex) {
                        logger.error("Cannot send event to event receiver : " + eventReceiver.getEventReceiverName(), ex);
                    }
                }
                return response;
            } else {
                throw new Exception("There is no current run to stop !");
            }
        }
    }

    /* (non-Javadoc)
     * @see org.easysoa.esperpoc.run.RunManager#record()
     */
    @Override
    public void record(ExchangeRecord exchangeRecord) {
        synchronized (RunManagerImpl.this) { // forbid simultaneous record, start, stop, save, delete
            if (this.currentRun != null) {
                // Get the current run and add a message
                logger.debug("Recording message : " + exchangeRecord);
                try {
                    exchangeRecord.getExchange().setExchangeID(Long.toString(exchangeNumberGenerator.getNextNumber()));
                    this.getCurrentRun().addExchange(exchangeRecord);
                } catch (Exception ex) {
                    logger.error("Unable to record message !", ex);
                }
            } else {
                logger.error("Unable to record message : There is no current run !");
            }
        }
    }

    /* (non-Javadoc)
     * @see org.easysoa.esperpoc.run.RunManager#deleteRun()
     */
    @Override
    public String delete() throws Exception {
        synchronized (RunManagerImpl.this) { // forbid simultaneous record, start, stop, save, delete
            if (this.currentRun != null) {
                currentRun = null;
                return "Run deleted !";
            } else {
                throw new Exception("There is no current run to delete !");
            }
        }
    }

    @Override
    public String save() throws Exception {
        synchronized (RunManagerImpl.this) { // forbid simultaneous record, start, stop, save, delete
            if (this.currentRun != null) {
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
     *
     * @param runName The run name to check
     * @return true if the run name is unique, false otherwise
     */
    private boolean checkUniqueRunName(String runName) {
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
        if (currentRun != null) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Set the auto start. The auto start feature create a new
     * <code>Run</code> automatically even if the start method was not called
     * before.
     *
     * @param autoStart true if a new run should be created automatically when
     * the method getCurrentRun is called, false otherwise
     */
    public void setAutoStart(boolean autoStart) {
        this.autoStart = autoStart;
    }

    /**
     * Returns true id the auto start is enabled
     *
     * @return true id the auto start is enabled
     */
    public boolean isAutoStart() {
        return autoStart;
    }

    /**
     * Set the auto save. The auto save feature save automatically the run when
     * the stop method is called.
     *
     * @param autoStart true if the current run should be saved automatically
     * when the method stop is called, false otherwise
     */
    public void setAutoSave(boolean autoSave) {
        this.autoSave = autoSave;
    }

    /**
     * Returns true id the auto save is enabled
     *
     * @return true id the auto save is enabled
     */
    public boolean isAutoSave() {
        return autoSave;
    }
}
