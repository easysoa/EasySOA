package org.easysoa.rest;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.easysoa.api.AutomationHelper;
import org.easysoa.api.EasySOAApiSession;
import org.easysoa.api.EasySOADocument;
import org.easysoa.api.EasySOARemoteDocument;
import org.easysoa.rest.RestNotificationFactory.RestDiscoveryService;
import org.json.JSONObject;
import org.nuxeo.ecm.automation.client.model.Document;
import org.nuxeo.ecm.automation.client.model.Documents;


/**
 *
 * @author mkalam-alami
 *
 */
public class EasySOARemoteApi implements EasySOAApiSession {

    private static final String AUTOMATION_URL = "http://localhost:8080/nuxeo/site/automation";

    private static Logger logger = Logger.getLogger(EasySOARemoteApi.class.getName());

    private RestNotificationFactory notificationFactory;

    private AutomationHelper automationHelper;

    public EasySOARemoteApi(String username, String password) throws IOException {
        this.notificationFactory = new RestNotificationFactory(username, password);
        this.automationHelper = new AutomationHelper(AUTOMATION_URL, username, password);
    }

    public EasySOARemoteApi(String nuxeoApisUrl, String username, String password) throws IOException {
        this.notificationFactory = new RestNotificationFactory(nuxeoApisUrl, username, password);
        this.automationHelper = new AutomationHelper(AUTOMATION_URL, username, password);
    }

    @Override
    public EasySOADocument notifyAppliImpl(Map<String, String> properties) {
        return sendRequest(RestDiscoveryService.APPLIIMPL, properties);
    }

    @Override
    public EasySOADocument notifyServiceApi(Map<String, String> properties) {
        return sendRequest(RestDiscoveryService.SERVICEAPI, properties);
    }

    @Override
    public EasySOADocument notifyService(Map<String, String> properties) {
        return sendRequest(RestDiscoveryService.SERVICE, properties);
    }

    @Override
    public EasySOADocument notifyServiceReference(Map<String, String> properties) {
        return sendRequest(RestDiscoveryService.SERVICEREFERENCE, properties);
    }

    private EasySOADocument sendRequest(RestDiscoveryService service, Map<String, String> properties) {
        try {
            RestNotificationRequest request = notificationFactory.createNotification(service);
            request.setProperties(properties);

            // XXX: Launches a 2nd request, not even secure since request
            // might be treated even before the model is updated
            JSONObject responseData = request.send();
            if (responseData != null) {
                return getDocumentById((String) request.send().get("documentId"));
            }
            else {
                return null;
            }
        } catch (Exception e) {
            logger.warning(e.getMessage());
            return null;
        }
    }

    @Override
    public List<EasySOADocument> queryDocuments(String query) throws Exception {
        List<EasySOADocument> result = new LinkedList<EasySOADocument>();
        Documents documents = automationHelper.query(query);
        for (Document document : documents) {
            result.add(new EasySOARemoteDocument(document));
        }
        return result;
    }

    @Override
    public EasySOADocument getDocumentById(String id) throws Exception {
        return new EasySOARemoteDocument(automationHelper.getDocumentById(id));
    }

}
