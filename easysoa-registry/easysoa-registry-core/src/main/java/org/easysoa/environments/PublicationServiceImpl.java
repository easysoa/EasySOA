package org.easysoa.environments;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.easysoa.services.DeletedDocumentFilter;
import org.nuxeo.common.utils.IdUtils;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentModelList;
import org.nuxeo.ecm.core.api.DocumentRef;
import org.nuxeo.ecm.core.api.PathRef;

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
            DocumentModel targetEnvironment = null;
            DocumentModelList environments = session.getChildren(sectionsRootRef, "Section",
                    new DeletedDocumentFilter(), null);
            for (DocumentModel environment : environments) {
                if (environment.getTitle().equals(environmentName) 
                        && !"deleted".equals(environment.getCurrentLifeCycleState())) {
                    targetEnvironment = environment;
                    break;
                }
            }
            
            if (targetEnvironment == null) {
                targetEnvironment = session.createDocumentModel(sectionsRootRef.toString(), IdUtils.generateStringId(), "Section");
                targetEnvironment.setProperty("dublincore", "title", environmentName); 
                session.createDocument(targetEnvironment);
                session.save();
            }

            // Removed existing publicated versions of the document
            boolean proxyExists = false;
            DocumentModelList proxies = session.getProxies(model.getRef(), null);
            for (DocumentModel proxy : proxies) {
                if (!proxy.getParentRef().equals(targetEnvironment.getParentRef())) {
                    session.removeDocument(proxy.getRef());
                }
                else {
                    proxyExists = true;
                }
            }
            
            // Document publication (creates a proxy of the latest version of the document, at the target location)
            if (!proxyExists) {
                session.publishDocument(model, targetEnvironment);
            }
            
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

}
