package org.easysoa.services;

import org.easysoa.doctypes.AppliImpl;
import org.easysoa.doctypes.Service;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentModelList;

public class ServicesRootMapperService {

    /**
     * Maps the URLs of all services from the given app by using their templates (Service.PROP_URLTEMPLATE)
     * @param session
     * @param appliImplModel
     * @throws ClientException
     */
    public void mapUrls(CoreSession session, DocumentModel appliImplModel) throws ClientException {
        String servicesRootUrl = (String) appliImplModel.getProperty(AppliImpl.SCHEMA, AppliImpl.PROP_URL);
        DocumentModelList serviceModels = session.query("SELECT * FROM Service WHERE ecm:path STARTSWITH '" 
                + appliImplModel.getPathAsString() + "' AND ecm:currentLifeCycleState <> 'deleted'");
        for (DocumentModel serviceModel : serviceModels) {
            mapUrl(session, serviceModel, servicesRootUrl);
        }
    }

    /**
     * Maps the URL of the given service by using its template and the given service root URL
     * @param session
     * @param serviceModel
     * @param servicesRootUrl
     * @throws ClientException
     */
    private void mapUrl(CoreSession session, DocumentModel serviceModel, String servicesRootUrl) throws ClientException {
        String urlTemplate = (String) serviceModel.getProperty(Service.SCHEMA, Service.PROP_URLTEMPLATE);
        if (urlTemplate != null && !urlTemplate.isEmpty()) {
            String newUrl = servicesRootUrl + urlTemplate;
            serviceModel.setProperty(Service.SCHEMA, Service.PROP_URL, newUrl);
            session.saveDocument(serviceModel);
            session.save();
        }
    }

}
