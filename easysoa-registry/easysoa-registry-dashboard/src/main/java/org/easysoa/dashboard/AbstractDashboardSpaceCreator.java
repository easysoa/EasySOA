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

import static org.nuxeo.opensocial.container.shared.layout.enume.YUITemplate.YUI_ZT_50_50;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.nuxeo.common.utils.Path;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentRef;
import org.nuxeo.ecm.core.api.PathRef;
import org.nuxeo.ecm.core.api.UnrestrictedSessionRunner;
import org.nuxeo.ecm.core.management.storage.DocumentStoreManager;
import org.nuxeo.ecm.platform.usermanager.UserManager;
import org.nuxeo.ecm.spaces.api.Space;
import org.nuxeo.ecm.spaces.helper.WebContentHelper;
import org.nuxeo.opensocial.container.shared.layout.api.LayoutHelper;
import org.nuxeo.runtime.api.Framework;

/**
 * Common methods used for creating the EasySOA dashboard
 * space in unrestricted sessions.
 * 
 * @author mkalam-alami
 */
public abstract class AbstractDashboardSpaceCreator extends
        UnrestrictedSessionRunner {

    // Customized from
    // org.nuxeo.ecm.user.center.dashboard.AbstractDashboardSpaceCreator (bundle nuxeo-user-dashboard-opensocial)
    // See method initializeGadgets()
    
    public static final String DASHBOARD_MANAGEMENT_NAME = "dashboard-management";

    public static final String DASHBOARD_MANAGEMENT_PATH = DocumentStoreManager.MANAGEMENT_ROOT_PATH
            + "/" + DASHBOARD_MANAGEMENT_NAME;

    public static final String DASHBOARD_MANAGEMENT_TYPE = "HiddenFolder";

    protected Map<String, String> parameters = new HashMap<String, String>();

    protected AbstractDashboardSpaceCreator(CoreSession session,
            Map<String, String> parameters) {
        super(session);
        this.parameters = parameters;
    }

    /**
     * Returns the dashboard management document, creates it if needed.
     */
    protected DocumentModel getDashboardManagement() throws ClientException {
        String dashboardManagementPath = new Path(
                DocumentStoreManager.MANAGEMENT_ROOT_PATH).append(
                DASHBOARD_MANAGEMENT_NAME).toString();
        DocumentRef dashboardManagementPathRef = new PathRef(
                dashboardManagementPath);
        DocumentModel dashboardManagement;
        if (!session.exists(dashboardManagementPathRef)) {
            dashboardManagement = session.createDocumentModel(
                    DocumentStoreManager.MANAGEMENT_ROOT_PATH,
                    DASHBOARD_MANAGEMENT_NAME, DASHBOARD_MANAGEMENT_TYPE);
            return session.createDocument(dashboardManagement);
        } else {
            return session.getDocument(dashboardManagementPathRef);
        }
    }

    protected void addInitialGadgets(DocumentModel anonymousDashboardSpace)
            throws ClientException {
        Space space = anonymousDashboardSpace.getAdapter(Space.class);
        initializeLayout(space);
        String userLanguage = parameters.get("userLanguage");
        Locale locale = userLanguage != null ? new Locale(userLanguage) : null;
        initializeGadgets(space, session, locale);
    }

    protected void initializeLayout(Space space) throws ClientException {
        space.initLayout(LayoutHelper.buildLayout(YUI_ZT_50_50, YUI_ZT_50_50,
                YUI_ZT_50_50));
    }

    protected void initializeGadgets(Space space, CoreSession session,
            Locale locale) throws ClientException {
        // Here is defined the default dashboard layout.
        WebContentHelper.createOpenSocialGadget(space, session, locale,
                "servicestats", 0, 0, 0);
        WebContentHelper.createOpenSocialGadget(space, session, locale,
                "quicksearch", 0, 1, 0);
        WebContentHelper.createOpenSocialGadget(space, session, locale,
                "userdocuments", 0, 1, 1);
    }

    protected UserManager getUserManager() throws ClientException {
        try {
            return Framework.getService(UserManager.class);
        } catch (Exception e) {
            throw new ClientException("Unable to retrieve UserManager service",
                    e);
        }
    }

}
