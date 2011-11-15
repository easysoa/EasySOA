package org.easysoa.api;

import java.util.Map;

import org.easysoa.api.EasySOAApi;
import org.easysoa.services.DiscoveryService;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.runtime.api.Framework;

/**
 * 
 * Thin layer between the EasySOAApi and the DiscoveryService.
 * (XXX: They should eventually be merged)
 * 
 * @author mkalam-alami
 *
 */
public class EasySOALocalApi implements EasySOAApi {

    private DiscoveryService service;
    
    private CoreSession session;
    
    public EasySOALocalApi(CoreSession session) throws Exception {
        this.service = Framework.getService(DiscoveryService.class);
        this.session = session;
    }
    
    @Override
    public boolean notifyAppliImpl(Map<String, String> properties) {
        try {
            service.notifyAppliImpl(session, properties);
            return true;
        }
        catch (Exception e) { 
            return false;
        }
    }

    @Override
    public boolean notifyServiceApi(Map<String, String> properties) {
        try {
            service.notifyServiceApi(session, properties);
            return true;
        }
        catch (Exception e) { 
            return false;
        }
    }

    @Override
    public boolean notifyService(Map<String, String> properties) {
        try {
            service.notifyService(session, properties);
            return true;
        }
        catch (Exception e) { 
            return false;
        }
    }

    @Override
    public boolean notifyServiceReference(Map<String, String> properties) {
        try {
            service.notifyServiceReference(session, properties);
            return true;
        }
        catch (Exception e) { 
            return false;
        }
    }

}
