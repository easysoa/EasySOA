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

package org.easysoa.dashboard;

import static org.nuxeo.ecm.spaces.api.Constants.SPACE_DOCUMENT_TYPE;

import java.util.Map;

import org.nuxeo.common.utils.Path;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentRef;
import org.nuxeo.ecm.core.api.PathRef;
import org.nuxeo.ecm.core.api.security.ACE;
import org.nuxeo.ecm.core.api.security.ACL;
import org.nuxeo.ecm.core.api.security.ACP;
import org.nuxeo.ecm.core.api.security.SecurityConstants;

/**
 * Creates the default EasySOA dashboard {@code Space} in an Unrestricted Session.
 *
 * @author mkalam-alami
 */
public class DefaultDashboardSpaceCreator extends AbstractDashboardSpaceCreator {

    // Customized from
    // org.nuxeo.ecm.user.center.dashboard.DefaultDashboardSpaceCreator (bundle nuxeo-user-dashboard-opensocial)
    // See method AbstractDashboardSpaceCreator.initializeGadgets()
    
    public static final String DEFAULT_DASHBOARD_SPACE_NAME = "defaultDashboardSpace";

   // private static final Log log = LogFactory.getLog(DefaultDashboardSpaceCreator.class);

    public DocumentRef defaultDashboardSpaceRef;

    public DefaultDashboardSpaceCreator(CoreSession session,
            Map<String, String> parameters) {
        super(session, parameters);
    }

    @Override
    public void run() throws ClientException {
        DocumentModel dashboardManagement = getDashboardManagement();
        String defaultDashboardSpacePath = new Path(
                dashboardManagement.getPathAsString()).append(
                DEFAULT_DASHBOARD_SPACE_NAME).toString();
        DocumentRef defaultDashboardSpacePathRef = new PathRef(
                defaultDashboardSpacePath);

        DocumentModel defaultDashboardSpace;
        if (!session.exists(defaultDashboardSpacePathRef)) {
            defaultDashboardSpace = createDefaultDashboardSpace(dashboardManagement.getPathAsString());
        } else {
            defaultDashboardSpace = session.getDocument(defaultDashboardSpacePathRef);
        }
        defaultDashboardSpaceRef = defaultDashboardSpace.getRef();
    }

    protected DocumentModel createDefaultDashboardSpace(
            String dashboardManagementPath) throws ClientException {
        DocumentModel defaultDashboardSpace = session.createDocumentModel(
                dashboardManagementPath, DEFAULT_DASHBOARD_SPACE_NAME,
                SPACE_DOCUMENT_TYPE);
        defaultDashboardSpace.setPropertyValue("dc:title",
                "default dashboard space");
        defaultDashboardSpace.setPropertyValue("dc:description",
                "default dashboard space");
        defaultDashboardSpace = session.createDocument(defaultDashboardSpace);

        addInitialGadgets(defaultDashboardSpace);
        addDefaultACP(defaultDashboardSpace);
        return session.saveDocument(defaultDashboardSpace);
    }

    protected void addDefaultACP(DocumentModel defaultDashboardSpace)
            throws ClientException {
        ACP acp = defaultDashboardSpace.getACP();
        ACL acl = acp.getOrCreateACL();
        for (String group : getUserManager().getAdministratorsGroups())
            acl.add(new ACE(group, SecurityConstants.EVERYTHING, true));
        defaultDashboardSpace.setACP(acp, true);
    }

}
