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

import java.util.LinkedList;
import java.util.List;

import javax.faces.model.SelectItem;

import org.easysoa.services.DeletedDocumentFilter;
import org.easysoa.services.DocumentService;
import org.easysoa.services.ServiceValidationService;
import org.easysoa.validation.EnvironmentValidationService;
import org.easysoa.validation.ExchangeReplayController;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Create;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Install;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentModelList;
import org.nuxeo.ecm.core.api.DocumentRef;
import org.nuxeo.ecm.platform.ui.web.api.NavigationContext;
import org.nuxeo.runtime.api.Framework;

/**
 * Environment validation form
 * 
 * @author mkalam-alami
 * 
 */
@Name("environmentValidation")
@Scope(ScopeType.CONVERSATION)
@Install(precedence = Install.FRAMEWORK)
public class EnvironmentValidationBean {
    
   // private static Log log = LogFactory.getLog(EnvironmentValidationBean.class);

    @In(create = true)
    NavigationContext navigationContext;
    
    @In(create = true, required = false)
    CoreSession documentManager;

    DocumentService docService;
    
    EnvironmentValidationService environmentValidationService;

    ExchangeReplayController exchangeReplayController = null;
    
    private String runName = null;

    private String environmentName = null;
    
    private String validationReport = "";
    
    @Create
    public void init() throws Exception {
        // Create session if needed
        if (documentManager == null) {
            documentManager = navigationContext.getOrCreateDocumentManager();
        }
        if (environmentValidationService == null) {
            environmentValidationService = Framework.getService(EnvironmentValidationService.class);
        }
        if (docService == null) {
            docService = Framework.getService(DocumentService.class);
        }
    }
    
    public boolean runValidation() throws ClientException {
        if (runName != null && environmentName != null) {
            validationReport = environmentValidationService.run(runName, environmentName);
        }
        else {
            validationReport = "Error: run name or environment name not set";
        }
        return false;
    }

    public List<SelectItem> getAvailableRunNames() throws Exception {
        if (exchangeReplayController == null) {
            exchangeReplayController = Framework.getService(ServiceValidationService.class).getExchangeReplayController();
            if (exchangeReplayController == null) {
                return new LinkedList<SelectItem>();
            }
        }
        return BeanUtils.arrayToSelectItems(exchangeReplayController.getAllRunNames());
    }
    
    public String getRunName() {
        return runName;
    }
    
    public void setRunName(String runName) {
        this.runName = runName;
    }

    public List<SelectItem> getAvailableEnvironments() throws ClientException {
        DocumentModelList workspacesModels = getAllWorkspaces();
        List<String> workspaceNames = new LinkedList<String>();
        for (DocumentModel workspaceModel : workspacesModels) {
            workspaceNames.add(workspaceModel.getTitle());
        }
        return BeanUtils.arrayToSelectItems(workspaceNames.toArray());
    }
    
    public String getEnvironmentName() {
        return environmentName;
    }
    
    public void setEnvironmentName(String environmentName) {
        this.environmentName = environmentName;
    }
    
    public String getValidationReport() {
        return validationReport;
    }

    private DocumentModelList getAllWorkspaces() throws ClientException {
        DocumentRef workspaceRoot = docService.getWorkspaceRoot(documentManager);
        return documentManager.getChildren(workspaceRoot, "Workspace", new DeletedDocumentFilter(), null);
    }

}
