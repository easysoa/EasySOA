package org.easysoa.api;

import java.util.Map;

/**
 * 
 * @author mkalam-alami
 *
 */
public interface EasySOAApiSession {

    /**
     * Creates or updates an Appli Impl. given a set of properties
     * (see {@code org.easysoa.doctypes.AppliImpl} to know the available properties)
     * @param properties
     * @return The id of the produced document
     * @throws Exception
     */
    String notifyAppliImpl(Map<String, String> properties) throws Exception;

    /**
     * Creates or updates a Service API given a set of properties
     * (see {@code org.easysoa.doctypes.ServiceAPI} to know the available properties)
     * @param properties
     * @return The id of the produced document
     * @throws Exception
     */
    String notifyServiceApi(Map<String, String> properties) throws Exception;

    /**
     * Creates or updates a Service given a set of properties
     * (see {@code org.easysoa.doctypes.Service} to know the available properties)
     * @param properties
     * @return The id of the produced document
     * @throws Exception
     */
    String notifyService(Map<String, String> properties) throws Exception;

    /**
     * Creates or updates a Service Reference given a set of properties
     * (see {@code org.easysoa.doctypes.ServiceReference} to know the available properties)
     * @param properties
     * @return The id of the produced document
     * @throws Exception
     */
    String notifyServiceReference(Map<String, String> properties) throws Exception;
    
    // TODO: Common way to describe documents for local and remote access?
    // Documents queryDocuments(String query);
    // Document getDocumentById(String id);
    
}
