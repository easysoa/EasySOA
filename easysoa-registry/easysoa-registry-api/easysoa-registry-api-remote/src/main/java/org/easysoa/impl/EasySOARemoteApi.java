package org.easysoa.impl;

import java.io.IOException;
import java.util.Map;
import java.util.logging.Logger;

import org.easysoa.api.EasySOAApi;
import org.easysoa.impl.RestNotificationFactory.RestDiscoveryService;

/**
 * 
 * @author mkalam-alami
 *
 */
public class EasySOARemoteApi implements EasySOAApi {
    
    private static Logger logger = Logger.getLogger(EasySOARemoteApi.class.getName());
    
    private RestNotificationFactory notificationFactory;
    
    public EasySOARemoteApi() throws IOException {
        this.notificationFactory = new RestNotificationFactory();
    }

    public EasySOARemoteApi(String nuxeoApisUrl) throws IOException {
        this.notificationFactory = new RestNotificationFactory(nuxeoApisUrl);
    }

    @Override
    public boolean notifyAppliImpl(Map<String, String> properties) {
        return sendRequest(RestDiscoveryService.APPLIIMPL, properties);
    }

    @Override
    public boolean notifyServiceApi(Map<String, String> properties) {
        return sendRequest(RestDiscoveryService.SERVICEAPI, properties);
    }

    @Override
    public boolean notifyService(Map<String, String> properties) {
        return sendRequest(RestDiscoveryService.SERVICE, properties);
    }

    @Override
    public boolean notifyServiceReference(Map<String, String> properties) {
        return sendRequest(RestDiscoveryService.SERVICEREFERENCE, properties);
    }
    
    private boolean sendRequest(RestDiscoveryService service, Map<String, String> properties) {
        try {
            RestNotificationRequest request = notificationFactory.createNotification(service);
            request.setProperties(properties);
            request.send();
            return true;
        } catch (Exception e) {
            logger.warning(e.getMessage());
            return false;
        }
    }

}
