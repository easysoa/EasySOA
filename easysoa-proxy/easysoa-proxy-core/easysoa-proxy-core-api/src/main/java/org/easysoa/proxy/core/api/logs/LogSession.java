package org.easysoa.proxy.core.api.logs;

import org.easysoa.proxy.core.api.reports.Report;

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