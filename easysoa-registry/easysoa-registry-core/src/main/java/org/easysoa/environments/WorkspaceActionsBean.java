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

package org.easysoa.environments;

import org.easysoa.doctypes.AppliImpl;
import org.easysoa.doctypes.Workspace;
import org.easysoa.services.DeletedDocumentFilter;
import org.easysoa.services.DocumentService;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Install;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.nuxeo.common.utils.IdUtils;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentModelList;
import org.nuxeo.ecm.platform.ui.web.api.NavigationContext;
import org.nuxeo.runtime.api.Framework;

/**
 * Publication actions
 * 
 * @author mkalam-alami
 * 
 */
@Name("easysoaWorkspaceActions")
@Scope(ScopeType.CONVERSATION)
@Install(precedence = Install.FRAMEWORK)
public class WorkspaceActionsBean {

    @In(create = true, required = false)
    CoreSession documentManager;

    @In(create = true)
    NavigationContext navigationContext;

    public void publishCurrentWorkspace() throws Exception {
        
        PublicationService publicationService = Framework.getService(PublicationService.class);
        DocumentModel currentDocModel = navigationContext.getCurrentDocument();
        
        if (currentDocModel.getType().equals(Workspace.DOCTYPE)) {
            DocumentModelList appModels = documentManager.getChildren(currentDocModel.getRef(), 
                    AppliImpl.DOCTYPE, new DeletedDocumentFilter(), null);
            for (DocumentModel appModel : appModels) {
                publicationService.publish(documentManager, appModel, currentDocModel.getTitle());
            }
        }
        else {
            throw new Exception("Cannot start publication: current document is not a workspace");
        }
        
    }
    
    public void forkCurrentWorkspace() throws Exception {
        
        DocumentService docService = Framework.getService(DocumentService.class);
        DocumentModel currentDocModel = navigationContext.getCurrentDocument();
        
        if (currentDocModel.getType().equals("Section")) {

            DocumentModel newWorkspace = documentManager.createDocumentModel(
                    docService.getWorkspaceRoot(documentManager).toString(),
                    IdUtils.generateStringId(), Workspace.DOCTYPE);
            newWorkspace.setProperty("dublincore", "title", currentDocModel.getTitle() + " (copy)");
            newWorkspace.setProperty(Workspace.SCHEMA, Workspace.PROP_REFERENCEDWORKSPACE, currentDocModel.getId());
            newWorkspace = documentManager.createDocument(newWorkspace);
            
            DocumentModelList appsToCopy = documentManager.getChildren(currentDocModel.getRef());
            for (DocumentModel appToCopy : appsToCopy) {
                documentManager.copy(appToCopy.getRef(), newWorkspace.getRef(), null);
            }
            
            documentManager.save();
        }
        else {
            throw new Exception("Cannot start fork: current document is not an environment");
        }
        
    }
       

}
