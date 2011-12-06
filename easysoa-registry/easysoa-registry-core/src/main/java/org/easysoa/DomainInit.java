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
import org.nuxeo.common.utils.IdUtils;
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
    public static final String SECTIONS_ROOT_TITLE = "Environments";

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
        DocumentModel sectionRoot = null;

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
            } 
            // Change Sections root title
            else if (rootChild.getType().equals("SectionRoot")) {
                sectionRoot = rootChild;
                sectionRoot.setProperty("dublincore", "title", SECTIONS_ROOT_TITLE);
                session.saveDocument(sectionRoot);
            }
            // Remove templates root (put it in the trash rather than deleting them since it is needed by Nuxeo)
            else if (!rootChild.getCurrentLifeCycleState().equals(LifeCycleConstants.DELETED_STATE)) {
                rootChild.followTransition(LifeCycleConstants.DELETE_TRANSITION);
                session.saveDocument(rootChild);
            }
        }
     
        DocumentService docService;
        try {
            docService = Framework.getService(DocumentService.class);

            // Touch default application
            docService.getDefaultAppliImpl(session); 
            
            // Create default environment if necessary
            if (sectionRoot != null && !session.hasChildren(sectionRoot.getRef())) {
               DocumentModel newSection = session.createDocumentModel(sectionRoot.getPathAsString(), IdUtils.generateStringId(), "Section");
               newSection.setProperty("dublincore", "title", "Master");
               session.createDocument(newSection);
            }
        } catch (Exception e) {
            log.warn("Failed to make sure default application exists", e);
        }
        
        session.save();

    }

}