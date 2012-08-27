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

/**
 * 
 */
package org.easysoa.proxy.core.api.logs;

import java.util.HashMap;
import java.util.Set;

import org.apache.log4j.Logger;
import org.easysoa.proxy.core.api.logs.LogEngine;
import org.easysoa.proxy.core.api.logs.LogSession;
import org.easysoa.proxy.core.api.reports.Report;
import org.osoa.sca.annotations.Scope;

/**
 * Implementation for log engine
 * 
 * @author jguillemotte
 *
 */
@Scope("composite")
public class LogEngineImpl implements LogEngine {

    // Produce specialized logs and reports
    
    // Logger for reports
    // Specific logger for assertions
    private static final Logger reportLogger = Logger.getLogger("reportLogger");    
    
    // Log session store 
    private HashMap<String, LogSession> logSessions;
    
    public LogEngineImpl(){
        this.logSessions = new HashMap<String, LogSession>();
    }
    
    /**
     * 
     * @param logSessionName Name of log session
     * @throws Exception If a problem occurs
     */
    public void startLogSession(String logSessionName) throws Exception {
        startLogSession(logSessionName, null);
    }

    /**
     * @param logSessionName Name of log session
     * @param report Report associated to the log session
     * @throws Exception If a problem occurs
     */
    public void startLogSession(String logSessionName, Report report) throws Exception {
        // Start a new log session
        if(logSessionName == null || "".equals(logSessionName)){
            throw new IllegalArgumentException("logSessionName parameter must not be null or empty");
        }
        if(logSessions.containsKey(logSessionName)){
            throw new IllegalArgumentException("logSessionName already exists, cannot start another logSession with the same name");
        }
        this.logSessions.put(logSessionName, new LogSessionImpl(report));
    }
    
    /**
     * 
     * @param logSessionName
     */
    public void saveAndRemoveLogSession(String logSessionName) throws Exception{
        logSessions.get(logSessionName).save();
        logSessions.remove(logSessionName);
    }
    
    /**
     * 
     * @param logSessionName
     */
    public void saveLogSession(String logSessionName) throws Exception{
        logSessions.get(logSessionName).save();
    }
    
    /**
     * 
     * @param logSessionName
     * @return
     */
    public LogSession getLogSession(String logSessionName){
        return this.logSessions.get(logSessionName);
    }
    
    /**
     * Returns a set containing the log session names
     * @return
     */
    public Set<String> getLogSessions(){
        return this.logSessions.keySet();
    }
    
}
