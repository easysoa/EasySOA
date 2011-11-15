package org.easysoa.api;

import java.util.Map;

/**
 * 
 * @author mkalam-alami
 *
 */
public interface EasySOAApi {

    boolean notifyAppliImpl(Map<String, String> properties);

    boolean notifyServiceApi(Map<String, String> properties);

    boolean notifyService(Map<String, String> properties);

    boolean notifyServiceReference(Map<String, String> properties);
    
    // TODO: Common way to describe documents for local and remote access?
    // Documents queryDocuments(String query);
    
}
