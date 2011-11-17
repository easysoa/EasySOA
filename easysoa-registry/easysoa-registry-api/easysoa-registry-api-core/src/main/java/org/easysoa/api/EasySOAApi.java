package org.easysoa.api;

import java.util.Map;

/**
 * 
 * @author mkalam-alami
 *
 */
public interface EasySOAApi {

    boolean notifyAppliImpl(Map<String, String> properties) throws Exception;

    boolean notifyServiceApi(Map<String, String> properties) throws Exception;

    boolean notifyService(Map<String, String> properties) throws Exception;

    boolean notifyServiceReference(Map<String, String> properties) throws Exception;
    
    // TODO: Common way to describe documents for local and remote access?
    // Documents queryDocuments(String query);
    
}
