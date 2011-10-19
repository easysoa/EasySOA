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

package org.easysoa.test;

import org.easysoa.DomainInit;
import org.easysoa.doctypes.AppliImpl;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.test.annotations.RepositoryInit;

/**
 * Default repository initializer that create the default DM doc hierarchy.
 */
public class EasySOARepositoryInit implements RepositoryInit {
    
    public void populate(CoreSession session) throws ClientException {
        
        DocumentModel doc = session.createDocumentModel("/", "default-domain",
                "Domain");
        doc.setProperty("dublincore", "title", DomainInit.DOMAIN_TITLE);
        doc = session.createDocument(doc);
        session.saveDocument(doc);

        doc = session.createDocumentModel("/default-domain/", "workspaces",
                "WorkspaceRoot");
        doc.setProperty("dublincore", "title", DomainInit.WORKSPACE_ROOT_TITLE);
        doc = session.createDocument(doc);
        session.saveDocument(doc);
        
        doc = session.createDocumentModel("/default-domain/workspaces", "(Default)",
                "Workspace");
        doc.setProperty("dublincore", "title", AppliImpl.DEFAULT_APPLIIMPL_TITLE);
        doc.setProperty(AppliImpl.SCHEMA, AppliImpl.PROP_URL, AppliImpl.DEFAULT_APPLIIMPL_URL);
        doc = session.createDocument(doc);
        session.saveDocument(doc);

    }

}
