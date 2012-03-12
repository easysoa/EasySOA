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
 *
 *
 * (C) Copyright 2010 Nuxeo SAS (http://nuxeo.com/) and contributors.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Lesser General Public License
 * (LGPL) version 2.1 which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/lgpl.html
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 * Contributors:
 *     Nuxeo - initial API and implementation
 */

package org.easysoa.dashboard;

import static org.nuxeo.ecm.spaces.api.Constants.SPACE_DOCUMENT_TYPE;

import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentRef;
import org.nuxeo.ecm.core.api.IdRef;
import org.nuxeo.ecm.core.api.PathRef;
import org.nuxeo.ecm.core.api.UnrestrictedSessionRunner;
import org.nuxeo.ecm.core.api.security.ACE;
import org.nuxeo.ecm.core.api.security.ACL;
import org.nuxeo.ecm.core.api.security.ACP;
import org.nuxeo.ecm.platform.userworkspace.api.UserWorkspaceService;
import org.nuxeo.ecm.spaces.api.AbstractSpaceProvider;
import org.nuxeo.ecm.spaces.api.Space;
import org.nuxeo.ecm.spaces.api.SpaceManager;
import org.nuxeo.ecm.spaces.api.exceptions.SpaceException;
import org.nuxeo.opensocial.container.server.webcontent.api.WebContentAdapter;
import org.nuxeo.opensocial.container.shared.webcontent.OpenSocialData;
import org.nuxeo.opensocial.container.shared.webcontent.WebContentData;
import org.nuxeo.opensocial.gadgets.helper.GadgetI18nHelper;
import org.nuxeo.runtime.api.Framework;

/**
 * Default User dashboard space provider.
 * <p>
 * If the user does not have any dashboard, it tries to copy the one configured
 * by the Administrator. If it fails, create an empty dashboard.
 *
 * @author mkalam-alami
 */
public class UserDashboardSpaceProvider extends AbstractSpaceProvider {

    // Customized from
    // org.nuxeo.ecm.user.center.dashboard.UserDashboardSpaceProvider (bundle nuxeo-user-dashboard-opensocial)
    // See method AbstractDashboardSpaceCreator.initializeGadgets()

    public static final String DEFAULT_DASHBOARD_SPACE_PROVIDER = "defaultDashboardSpaceProvider";

    public static final String USER_DASHBOARD_SPACE_NAME = "userDashboardSpace";

    private static final Log log = LogFactory.getLog(UserDashboardSpaceProvider.class);

    @Override
    protected Space doGetSpace(CoreSession session,
            DocumentModel contextDocument, String spaceName,
            Map<String, String> parameters) throws SpaceException {
        try {
            return getOrCreateSpace(session, parameters);
        } catch (ClientException e) {
            log.error("Unable to create or get personal dashboard", e);
            return null;
        }
    }

    protected Space getOrCreateSpace(CoreSession session,
            Map<String, String> parameters) throws ClientException {
        String userWorkspacePath = getUserPersonalWorkspace(session).getPathAsString();
        DocumentRef spaceRef = new PathRef(userWorkspacePath,
                USER_DASHBOARD_SPACE_NAME);
        if (session.exists(spaceRef)) {
            DocumentModel existingSpace = session.getDocument(spaceRef);
            return existingSpace.getAdapter(Space.class);
        } else {

            // EasySOA: Create default dashboard
            DefaultDashboardSpaceCreator spaceCreator = new DefaultDashboardSpaceCreator(session, parameters);
            spaceCreator.runUnrestricted();
            if (spaceCreator.defaultDashboardSpaceRef != null) {
                Space space = session.getDocument(spaceCreator.defaultDashboardSpaceRef)
                        .getAdapter(Space.class);
                if (space != null) {
                    i18nGadgets(space, session, parameters);
                    return space;
                }
            }

            // create an empty dashboard
            return createEmptyDashboard(userWorkspacePath, session);

        }
    }

    protected DocumentModel getUserPersonalWorkspace(CoreSession session)
            throws ClientException {
        try {
            UserWorkspaceService svc = Framework.getService(UserWorkspaceService.class);
            return svc.getCurrentUserPersonalWorkspace(session, null);
        } catch (Exception e) {
            throw new ClientException(e);
        }
    }

    protected void i18nGadgets(Space space, CoreSession session,
            Map<String, String> parameters) throws ClientException {
        List<WebContentData> webContentDatas = space.readWebContents();
        for (WebContentData data : webContentDatas) {
            String userLanguage = parameters.get("userLanguage");
            Locale locale = userLanguage != null ? new Locale(userLanguage)
                    : null;

            WebContentAdapter<?> adapter = session.getDocument(
                    new IdRef(data.getId())).getAdapter(WebContentAdapter.class);
            if (adapter != null) {
                String name = data instanceof OpenSocialData ? ((OpenSocialData) data).getGadgetName()
                        : data.getName();
                String title = GadgetI18nHelper.getI18nGadgetTitle(name, locale);
                adapter.setTitle(title);
                adapter.update();
            }
        }
        session.save();
    }

    protected Space createEmptyDashboard(String userWorkspacePath,
            CoreSession session) throws ClientException {
        DocumentModel model = session.createDocumentModel(userWorkspacePath,
                USER_DASHBOARD_SPACE_NAME, SPACE_DOCUMENT_TYPE);
        model.setPropertyValue("dc:title", "nuxeo user dashboard space");
        model.setPropertyValue("dc:description", "user dashboard space");
        model = session.createDocument(model);
        return model.getAdapter(Space.class);
    }

    @Override
    public boolean isReadOnly(CoreSession session) {
        return true;
    }

    public static class DefaultDashboardSpaceCopy extends
            UnrestrictedSessionRunner {

        protected Map<String, String> parameters;

        protected String userWorkspacePath;

        public DocumentRef copiedSpaceRef;

        protected DefaultDashboardSpaceCopy(CoreSession session,
                Map<String, String> parameters, String userWorkspacePath) {
            super(session);
            this.parameters = parameters;
            this.userWorkspacePath = userWorkspacePath;
        }

        @Override
        public void run() throws ClientException {
            SpaceManager spaceManager = getSpaceManager();
            Space defaultSpace = spaceManager.getSpace(
                    DEFAULT_DASHBOARD_SPACE_PROVIDER, session, null, null,
                    parameters);
            if (defaultSpace != null) {
                DocumentModel newSpace = session.copy(
                        new IdRef(defaultSpace.getId()), new PathRef(
                                userWorkspacePath), USER_DASHBOARD_SPACE_NAME);
                newSpace.setPropertyValue("dc:title", "user dashboard space");
                newSpace.setPropertyValue("dc:description",
                        "user dashboard space");

                ACP acp = newSpace.getACP();
                ACL acl = acp.getOrCreateACL();
                for (ACE ace : acl.getACEs()) {
                    acl.remove(ace);
                }
                newSpace.setACP(acp, true);

                session.saveDocument(newSpace);
                session.save();
                copiedSpaceRef = newSpace.getRef();
            }
        }

        protected SpaceManager getSpaceManager() throws ClientException {
            try {
                return Framework.getService(SpaceManager.class);
            } catch (Exception e) {
                throw new ClientException(e);
            }
        }
    }

}
