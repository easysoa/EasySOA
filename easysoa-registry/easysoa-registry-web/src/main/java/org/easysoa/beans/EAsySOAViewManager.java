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

/*
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
 *
 * Contributors:
 *     Nuxeo - initial API and implementation
 */

package org.easysoa.beans;

import static org.jboss.seam.ScopeType.CONVERSATION;

import java.io.Serializable;
import java.util.List;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Factory;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.contexts.Contexts;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.platform.actions.Action;
import org.nuxeo.ecm.platform.ui.web.api.NavigationContext;
import org.nuxeo.ecm.platform.ui.web.api.TabActionsSelection;
import org.nuxeo.ecm.platform.ui.web.api.WebActions;

/**
 * Seam Bean used to manage navigation inside the EasySOA Main Tab.
 *
 * @author tiry
 */
@Name("easysoaViews")
@Scope(CONVERSATION)
public class EAsySOAViewManager implements Serializable {

    private static final long serialVersionUID = 1L;

    public static final String EASYSOATAB_ACTION_CATEGORY = "EASYSOA_TAB";

    public static final String VIEW_EASYSOA = "view_easysoa";

    @In(create = true, required = false)
    protected WebActions webActions;

    @In(create = true, required = false)
    protected transient NavigationContext navigationContext;

    protected DocumentModel lastVisitedDocument;

    public String goHome() {
        webActions.resetCurrentTabs(EASYSOATAB_ACTION_CATEGORY);
        Contexts.getEventContext().remove("currentEasysoaView");
        Contexts.getEventContext().remove("currentEasysoaSubView");
        return VIEW_EASYSOA;
    }

    public String enter() {
        lastVisitedDocument = navigationContext.getCurrentDocument();
        return VIEW_EASYSOA;
    }

    public String exit() throws ClientException {
        if (lastVisitedDocument != null) {
            return navigationContext.navigateToDocument(lastVisitedDocument);
        } else {
            return navigationContext.goHome();
        }
    }

    @Factory(value = "currentEasysoaView", scope = ScopeType.EVENT)
    public Action getCurrentView() {
        return webActions.getCurrentTabAction(EASYSOATAB_ACTION_CATEGORY);
    }

    public void setCurrentView(Action currentView) {
        webActions.setCurrentTabAction(EASYSOATAB_ACTION_CATEGORY, currentView);
    }

    public String getCurrentViewId() {
        return getCurrentView().getId();
    }

    public String setCurrentViewId(String currentViewId) {
        webActions.setCurrentTabId(EASYSOATAB_ACTION_CATEGORY, currentViewId);
        return VIEW_EASYSOA;
    }

    @Factory(value = "currentEasysoaSubView", scope = ScopeType.EVENT)
    public Action getCurrentSubView() {
        return webActions.getCurrentSubTabAction(getCurrentViewId());
    }

    public void setCurrentSubView(Action currentSubView) {
        webActions.setCurrentTabAction(
                TabActionsSelection.getSubTabCategory(getCurrentViewId()),
                currentSubView);
    }

    @Factory(value = "currentEasysoaSubViewId", scope = ScopeType.EVENT)
    public String getCurrentSubViewId() {
        return getCurrentSubView().getId();
    }

    public void setCurrentSubViewId(String currentSubViewId) {
        webActions.setCurrentTabId(
                TabActionsSelection.getSubTabCategory(getCurrentViewId()),
                currentSubViewId);
    }

    public List<Action> getAvailableActions() {
        return webActions.getActionsList(EASYSOATAB_ACTION_CATEGORY);
    }

    public List<Action> getAvailableSubActions() {
        return webActions.getActionsList(TabActionsSelection.getSubTabCategory(getCurrentViewId()));
    }
}
