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

import java.util.Set;

import org.easysoa.proxy.core.api.reports.Report;

/**
 * @author jguillemotte
 *
 */
public interface LogEngine {
    
    // Or report engine ???
    
    /**
     * Generate a report with assertion results and record it in a report file
     * @param assertionResults List of assertion result
     * @throws Exception If a problem occurs
     */
    //public void generateAssertionReport(List<AssertionResult> assertionResults) throws Exception;

    
    //public void saveReport(AssertionReport assertionReport) throws Exception;
    
 
    public void startLogSession(String logSessionName, Report report) throws Exception;
    
    public void saveAndRemoveLogSession(String logSessionName) throws Exception;
    
    public void saveLogSession(String logSessionName) throws Exception;
    
    public LogSession getLogSession(String logSessionName);
    
    public Set<String> getLogSessions();
    
}
