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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.easysoa.doctypes.AppliImpl;
import org.easysoa.doctypes.Workspace;
import org.easysoa.services.DeletedDocumentFilter;
import org.easysoa.services.DirectChildrenDocumentFilter;
import org.easysoa.services.DocumentService;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Install;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.nuxeo.common.utils.IdUtils;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentModelList;
import org.nuxeo.ecm.core.api.DocumentRef;
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

    protected static final Log log = LogFactory.getLog(WorkspaceActionsBean.class);
    
    @In(create = true, required = false)
    CoreSession documentManager;

    @In(create = true)
    NavigationContext navigationContext;

    public void publishCurrentWorkspace() throws Exception {
        
        long t = System.currentTimeMillis();
        
        PublicationService publicationService = Framework.getService(PublicationService.class);
        DocumentModel currentDocModel = navigationContext.getCurrentDocument();
        
        if (currentDocModel.getType().equals(Workspace.DOCTYPE)) {
            DocumentModelList appModels = documentManager.getChildren(currentDocModel.getRef(), 
                    AppliImpl.DOCTYPE, new DeletedDocumentFilter(), null);
            for (DocumentModel appModel : appModels) {
                publicationService.publish(documentManager, appModel, currentDocModel.getTitle());
            }
            
            long dt = System.currentTimeMillis() - t;
            log.info("Publication done in " + dt + "ms");
        }
        else {
            throw new Exception("Cannot start publication: current document is not a workspace");
        }
        
    }
    
    public void forkCurrentWorkspace() throws Exception {

        long t = System.currentTimeMillis();
        
        DocumentService docService = Framework.getService(DocumentService.class);
        DocumentModel currentDocModel = navigationContext.getCurrentDocument();
        
        if (currentDocModel.getType().equals("Section")) {

            // Create destination workspace
            DocumentModel newWorkspace = documentManager.createDocumentModel(
                    docService.getWorkspaceRoot(documentManager).toString(),
                    IdUtils.generateStringId(), Workspace.DOCTYPE);
            newWorkspace.setProperty("dublincore", "title", currentDocModel.getTitle() + " (copy)");
            newWorkspace.setProperty(Workspace.SCHEMA, Workspace.PROP_REFERENCEDENVIRONMENT, currentDocModel.getId());
            newWorkspace = documentManager.createDocument(newWorkspace);
            
            // Copy applications and their contents
            DocumentModelList appsToCopy = documentManager.getChildren(currentDocModel.getRef());
            for (DocumentModel appToCopy : appsToCopy) {
                copyRecursive(appToCopy.getRef(), newWorkspace.getRef());
            }
            
            documentManager.save();
            
            long dt = System.currentTimeMillis() - t;
            log.info("Forking done in " + dt + "ms");
        }
        else {
            throw new Exception("Cannot start fork: current document is not an environment");
        }
        
    }
    
    private DocumentModel copyRecursive(DocumentRef from, DocumentRef toFolder) throws ClientException {
        DocumentModel newDoc = documentManager.copy(from, toFolder, null);
        DocumentModelList children = documentManager.getChildren(from, null, new DirectChildrenDocumentFilter(from), null);
        for (DocumentModel child : children) {
            copyRecursive(child.getRef(), newDoc.getRef());
        }
        return newDoc;
    }
       

}
