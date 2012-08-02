package org.easysoa.logs;

import org.easysoa.reports.Report;

public interface LogSession {

    /**
     * 
     * @param report
     */
    public abstract void setReport(Report report);

    /**
     * 
     * @return
     */
    public abstract Report getReport();

    /**
     * Save the logSession
     * @throws Exception
     */
    public abstract void save() throws Exception;

}