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
package org.easysoa.logs;

import org.easysoa.records.persistence.filesystem.ProxyFileStore;
import org.easysoa.reports.Report;

/**
 * Contains all the stuff required for a log session
 * - Loggers
 * - reports
 * - ...
 * 
 * @author jguillemotte
 *
 */
public class LogSession {
    
    // Report
    private Report report;
    
    /**
     * Default constructor
     */
    public LogSession(){
        this(null);
    }

    /**
     * 
     * @param report
     */
    public LogSession(Report report){
        this.report = report;
    }
    
    /**
     * 
     * @param report
     */
    public void setReport(Report report){
        this.report = report;
    }
    
    /**
     * 
     * @return
     */
    public Report getReport(){
        return this.report;
    }
    
    /**
     * Save the logSession
     * @throws Exception
     */
    public void save() throws Exception {
        // Call the File store
        ProxyFileStore erfs = new ProxyFileStore();
        erfs.saveReport(report);
    }
    
}
