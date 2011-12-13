package org.easysoa.environments;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.easysoa.doctypes.AppliImpl;
import org.easysoa.doctypes.Service;
import org.easysoa.doctypes.ServiceAPI;
import org.easysoa.doctypes.ServiceReference;
import org.easysoa.services.DeletedDocumentFilter;
import org.easysoa.services.DocumentService;
import org.easysoa.services.PublicationService;
import org.nuxeo.common.utils.IdUtils;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentModelList;
import org.nuxeo.ecm.core.api.DocumentRef;
import org.nuxeo.ecm.core.api.PathRef;
import org.nuxeo.ecm.core.api.model.PropertyException;
import org.nuxeo.runtime.api.Framework;

/**
 * 
 * @author mkalam-alami
 *
 */
public class PublicationServiceImpl implements PublicationService {
    
    private static final Log log = LogFactory.getLog(PublicationServiceImpl.class);

    private DocumentRef sectionsRootRef = new PathRef("/default-domain/sections");

    public void publish(CoreSession session, DocumentModel model, String environmentName) {
        try {
            // Find or create target environment
            DocumentService docService = Framework.getService(DocumentService.class);
            DocumentModel targetEnvironment = docService.findEnvironment(session, environmentName);
            if (targetEnvironment == null) {
                targetEnvironment = session.createDocumentModel(sectionsRootRef.toString(), IdUtils.generateStringId(), "Section");
                targetEnvironment.setProperty("dublincore", "title", environmentName); 
                targetEnvironment = session.createDocument(targetEnvironment);
                session.save();
            }

            // Document publication (creates a proxy of the latest version of the document and its children, at the target location)
            if (AppliImpl.DOCTYPE.equals(model.getType())) {
                publishAppliImpl(session, model, targetEnvironment);
            }
            else if (ServiceAPI.DOCTYPE.equals(model.getType())) {
                DocumentModel publishedAppliImplModel = findParentProxy(session, model, targetEnvironment);
                publishApi(session, model, targetEnvironment, publishedAppliImplModel);
            }
            else if (Service.DOCTYPE.equals(model.getType())) {
                DocumentModel publishedApiModel = findParentProxy(session, model, targetEnvironment);
                publishService(session, model, targetEnvironment, publishedApiModel);
            }
            else if (ServiceReference.DOCTYPE.equals(model.getType())) {
                DocumentModel publishedAppliImplModel = findParentProxy(session, model, targetEnvironment);
                publishServiceReference(session, model, targetEnvironment, publishedAppliImplModel);
            }
            
            // Save
            session.save();
            
            /*
             * For more detailed information on the publication mechanics, see:
             * - org.nuxeo.ecm.core.api.AbstractSession.publishDocument()
             * - org.nuxeo.ecm.platform.publisher.api.PublisherService
             * - org.nuxeo.ecm.platform.publisher.web.PublishActionsBean
             * - org.nuxeo.ecm.platform.publisher.jbpm.CoreProxyWithWorkflowFactory.DocumentPublisherUnrestricted
             */

        } catch (Exception e) {
            log.error(e);
        }
    }
    
    @Override
    public void unpublish(CoreSession session, DocumentModel model, String environmentName) {
        try {
            // Find environment
            DocumentService docService = Framework.getService(DocumentService.class);
            DocumentModel targetEnvironment = docService.findEnvironment(session, environmentName);
            
            // Unpublish
            if (targetEnvironment != null) {
                removeExistingPublishedVersions(session, model, targetEnvironment);
            }
        } catch (Exception e) {
            log.error(e);
        }
    }

    @Override
    public void unpublish(CoreSession session, DocumentModel model) {
        try {
            DocumentModelList proxies = session.getProxies(model.getRef(), null);
            for (DocumentModel proxy : proxies) {
                session.removeDocument(proxy.getRef());
            }
        } catch (Exception e) {
            log.error(e);
        }
    }

    private DocumentModel findParentProxy(CoreSession session, DocumentModel model, DocumentModel targetEnvironment) throws PropertyException, ClientException {
        // Init doctype-specific info
        String doctypeToFind, urlField;
        if (Service.DOCTYPE.equals(model.getType())) {
            doctypeToFind = ServiceAPI.DOCTYPE;
            urlField = ServiceAPI.SCHEMA_PREFIX + ServiceAPI.PROP_URL;
        }
        else {
            doctypeToFind = AppliImpl.DOCTYPE;
            urlField = AppliImpl.SCHEMA_PREFIX + AppliImpl.PROP_URL;
        }
        // Run query
        DocumentModel workspaceParent = session.getDocument(model.getParentRef());
        DocumentModelList parentProxy = session.query("SELECT * FROM " + doctypeToFind + " WHERE " + urlField + " = '" + 
                workspaceParent.getProperty(urlField) + "' AND ecm:path STARTSWITH '" + targetEnvironment.getPathAsString() + "'");
        if (parentProxy != null && !parentProxy.isEmpty()) {
            return parentProxy.get(0);
        }
        else {
            return null;
        }
    }

    private DocumentModel publishAppliImpl(CoreSession session, DocumentModel appliImplModel, DocumentModel envModel) throws ClientException { 
        // Publish AppliImpl
        removeExistingPublishedVersions(session, appliImplModel, envModel);
        DocumentModel publishedAppliImplModel = doPublish(session, appliImplModel, envModel);
        
        // Publish children
        DocumentModelList apiModels = session.getChildren(appliImplModel.getRef(), 
                ServiceAPI.DOCTYPE, new DeletedDocumentFilter(), null);
        for (DocumentModel apiModel : apiModels) {
            publishApi(session, apiModel, envModel, publishedAppliImplModel);
        }
        DocumentModelList serviceReferenceModels = session.getChildren(appliImplModel.getRef(), 
                ServiceReference.DOCTYPE, new DeletedDocumentFilter(), null);
        for (DocumentModel serviceReferenceModel : serviceReferenceModels) {
            publishServiceReference(session, serviceReferenceModel, envModel, publishedAppliImplModel);
        }
        
        return publishedAppliImplModel;
    }

    private DocumentModel publishApi(CoreSession session, DocumentModel apiModel, DocumentModel envModel, DocumentModel publishedAppliImplModel) throws ClientException {
        if (publishedAppliImplModel == null) {
            // Publish parent document
            DocumentModel workspaceParentModel = session.getDocument(apiModel.getParentRef());
            if (ServiceAPI.DOCTYPE.equals(workspaceParentModel.getType())) {
                DocumentModel publishedGrandparentModel = findParentProxy(session, workspaceParentModel, envModel);
                publishedAppliImplModel = publishApi(session, workspaceParentModel, envModel, publishedGrandparentModel);
            }
            else {
                publishedAppliImplModel = publishAppliImpl(session, workspaceParentModel, envModel);
            }
        }
        
        // Publish API
        removeExistingPublishedVersions(session, apiModel, envModel);
        DocumentModel publishedApiModel = doPublish(session, apiModel, publishedAppliImplModel);
        
        // Publish children
        DocumentModelList childApiModels = session.getChildren(apiModel.getRef(), 
                ServiceAPI.DOCTYPE, new DeletedDocumentFilter(), null);
        for (DocumentModel childApiModel : childApiModels) {
            publishApi(session, childApiModel, envModel, publishedApiModel);
        }
        DocumentModelList childServiceModels = session.getChildren(apiModel.getRef(), 
                Service.DOCTYPE, new DeletedDocumentFilter(), null);
        for (DocumentModel childServiceModel : childServiceModels) {
            publishApi(session, childServiceModel, envModel, publishedApiModel);
        }
        
        return publishedApiModel;
    }

    private DocumentModel publishService(CoreSession session, DocumentModel serviceModel, DocumentModel envModel, DocumentModel publishedApiModel) throws ClientException {
        if (publishedApiModel == null) {
            // Publish parent API
            DocumentModel workspaceParentModel = session.getDocument(serviceModel.getParentRef());
            DocumentModel publishedGrandparentModel = findParentProxy(session, workspaceParentModel, envModel);
            publishedApiModel = publishApi(session, workspaceParentModel, envModel, publishedGrandparentModel);
        }
        
        // Publish service
        removeExistingPublishedVersions(session, serviceModel, envModel);
        return doPublish(session, serviceModel, publishedApiModel);
    }

    private DocumentModel publishServiceReference(CoreSession session, DocumentModel serviceReferenceModel,
            DocumentModel envModel, DocumentModel publishedAppliImplModel) throws ClientException {
        if (publishedAppliImplModel == null) {
            // Publish parent Appli Impl.
            DocumentModel workspaceParentModel = session.getDocument(serviceReferenceModel.getParentRef());
            publishedAppliImplModel = publishAppliImpl(session, workspaceParentModel, envModel);
        }
        
        // Publish service reference
        removeExistingPublishedVersions(session, serviceReferenceModel, envModel);
        return doPublish(session, serviceReferenceModel, publishedAppliImplModel);
    }
    
    private DocumentModel doPublish(CoreSession session, DocumentModel model, DocumentModel destParentModel) throws ClientException {
        DocumentModel publishedModel = session.publishDocument(model, destParentModel);
        // XXX Not sure why, but we have to undelete documents if they were previously published and deleted
        if ("deleted".equals(publishedModel.getCurrentLifeCycleState())) {
            session.followTransition(publishedModel.getRef(), "undelete");
        }
        return publishedModel;
    }

    private void removeExistingPublishedVersions(CoreSession session, DocumentModel model, DocumentModel envModel) throws ClientException {
        DocumentModelList proxies = session.getProxies(model.getRef(), null);
        for (DocumentModel proxy : proxies) {
            session.removeDocument(proxy.getRef());
        }
    }

}
