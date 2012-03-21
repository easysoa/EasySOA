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

package org.easysoa.beans;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.easysoa.services.PublicationService;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Install;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.platform.ui.web.api.NavigationContext;
import org.nuxeo.runtime.api.Framework;

/**
 * Publication and forking actions
 * 
 * @author mkalam-alami
 * 
 */
@Name("easysoaEnvironmentActions")
@Scope(ScopeType.CONVERSATION)
@Install(precedence = Install.FRAMEWORK)
public class EnvironmentActionsBean {

    protected static final Log log = LogFactory.getLog(EnvironmentActionsBean.class);
    
    @In(create = true, required = false)
    CoreSession documentManager;

    @In(create = true)
    NavigationContext navigationContext;
    
    PublicationService publicationService = null;

    public void publishCurrentWorkspace() throws Exception {
    	DocumentModel currentDocument = navigationContext.getCurrentDocument();
    	getPublicationService().publish(documentManager, currentDocument, currentDocument.getTitle());
    }
    
    public void forkCurrentEnvironment() throws Exception {
        DocumentModel currentDocModel = navigationContext.getCurrentDocument();
        getPublicationService().forkEnvironment(documentManager, currentDocModel);
    }
    
    public void updateApp() throws Exception {
        DocumentModel currentDocModel = navigationContext.getCurrentDocument();
        getPublicationService().updateFromReferenceEnvironment(documentManager, currentDocModel);
    }
    
    private PublicationService getPublicationService() throws Exception {
    	if (publicationService == null) {
    		publicationService = Framework.getService(PublicationService.class);
    	}
    	return publicationService;
    }
    
}
