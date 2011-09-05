package org.easysoa;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.easysoa.services.DocumentService;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.LifeCycleConstants;
import org.nuxeo.ecm.core.api.UnrestrictedSessionRunner;
import org.nuxeo.runtime.api.Framework;

/**
 * Creates all needed folders on Nuxeo startup.
 * 
 * @author mkalam-alami
 * 
 */
public class DomainInit extends UnrestrictedSessionRunner {

    public static final String DOMAIN_TITLE = "EasySOA";
    public static final String WORKSPACE_ROOT_TITLE = "Service Registry";

    private static Log log = LogFactory.getLog(DomainInit.class);

    public DomainInit(String repositoryName) {
        super(repositoryName);
    }

    /**
     * Sets up default domain
     */
    public void run() throws ClientException {

        DocumentModel root = session.getChildren(
                this.session.getRootDocument().getRef()).get(0);

        // Change root title
        if (!root.getTitle().equals(DOMAIN_TITLE)) {
            root.setProperty("dublincore", "title", DOMAIN_TITLE);
            session.saveDocument(root);
        }

        for (DocumentModel rootChild : session.getChildren(root.getRef())) {
            // Change workspace root title
            if (rootChild.getType().equals("WorkspaceRoot")) {
                rootChild.setProperty("dublincore", "title",
                        WORKSPACE_ROOT_TITLE);
                session.saveDocument(rootChild);
                session.save();
            } 
            // Remove unnecessary documents: sections root, templates root.
            // Put them in the trash rather than deleting them since they are needed by Nuxeo.
            else if (!rootChild.getCurrentLifeCycleState()
                    .equals(LifeCycleConstants.DELETED_STATE)) {
                rootChild.followTransition(LifeCycleConstants.DELETE_TRANSITION);
                session.saveDocument(rootChild);
            }
        }
     
        // Touch default application
        DocumentService docService;
        try {
            docService = Framework.getService(DocumentService.class);
            docService.getDefaultAppliImpl(session); 
        } catch (Exception e) {
            log.warn("Failed to make sure default application exists", e);
        }

        session.save();

    }

}