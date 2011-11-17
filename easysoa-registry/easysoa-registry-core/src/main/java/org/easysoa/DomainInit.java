/**
 * EasySOA Registry
 * Copyright 2011 Open Wide
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * 
 * Contact : easysoa-dev@googlegroups.com
 */

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
                rootChild.setProperty("dublincore", "title", WORKSPACE_ROOT_TITLE);
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