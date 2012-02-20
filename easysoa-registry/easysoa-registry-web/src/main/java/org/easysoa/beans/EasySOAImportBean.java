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

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.easysoa.doctypes.AppliImpl;
import org.easysoa.listeners.AppliImplListener;
import org.easysoa.sca.IScaImporter;
import org.easysoa.sca.extension.ScaImporterComponent;
import org.easysoa.sca.visitors.BindingVisitorFactory;
import org.easysoa.sca.visitors.LocalBindingVisitorFactory;
import org.easysoa.services.DeletedDocumentFilter;
import org.easysoa.services.DocumentService;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Create;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Install;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Observer;
import org.jboss.seam.annotations.Scope;
import org.nuxeo.ecm.core.api.Blob;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentModelList;
import org.nuxeo.ecm.core.api.DocumentRef;
import org.nuxeo.ecm.core.api.IdRef;
import org.nuxeo.ecm.core.api.impl.DocumentModelListImpl;
import org.nuxeo.ecm.platform.ui.web.api.NavigationContext;
import org.nuxeo.runtime.api.Framework;

/**
 * SCA Import form
 * 
 * @author mkalam-alami, mdutoo
 * 
 */
@Name("easysoaImport")
@Scope(ScopeType.CONVERSATION)
@Install(precedence = Install.FRAMEWORK)
public class EasySOAImportBean {
    
    private static Log log = LogFactory.getLog(EasySOAImportBean.class);

    @In(create = true, required = false)
    CoreSession documentManager;

    @In(create = true)
    NavigationContext navigationContext;

    DocumentService docService;
    
    @In
    FacesContext facesContext;

    private Blob file = null;

    private String importType = "sca";

    private String targetWorkspace;

    private String targetAppliImpl;
    
    private DocumentModel targetWorkspaceModel;
    
    @Create
    @Observer(value = { AppliImplListener.APPLI_IMPL_CHANGED })
    public void init() throws Exception {
        // Create session if needed
        if (documentManager == null) {
            documentManager = navigationContext.getOrCreateDocumentManager();
        }
        if (docService == null) {
            docService = Framework.getService(DocumentService.class);
        }
        
        // If possible choose workspace
        DocumentModel defaultWorkspace = docService.getDefaultWorkspace(documentManager);
        if (defaultWorkspace != null) {
            setTargetWorkspace(defaultWorkspace);
        }
    }
    
    public void runImport() {
        if ("sca".equals(importType)) {
            importSCA();
        }
        else if ("soapui".equals(importType)) {
            importSoapUIConf();
        }
    }

    private void importSoapUIConf() {
        
        log.info("SOAP UI import");
        // TODO
        
    }

    private void importSCA() {
        
        // by choosing a stack (api server) type (frascati...)
        // (possibly initialized using composite info), then customizing it
        String serviceStackType = "FraSCAti"; // TODO get it from wizard
        String serviceStackUrl = "/"; // TODO get it from wizard
        
        if (file != null) {
            
            File scaFile = null;
    
            try {
                // Transfer composite to temporary file
                if (file.getFilename().lastIndexOf(".") == -1) {
                    log.warn("Chosen file has no extension, file type might not be recognized.");
                }
                scaFile = File.createTempFile("scaimport", file.getFilename());
                file.transferTo(scaFile);
    
                BindingVisitorFactory visitorFactory = new LocalBindingVisitorFactory(documentManager);
                IScaImporter importer = Framework.getService(ScaImporterComponent.class).createScaImporter(visitorFactory, scaFile);
    
                DocumentModel appliImplModel = documentManager.getDocument(new IdRef(targetAppliImpl));
                if (targetAppliImpl != null) {
                    // Add a test here to check if instance of NuxeoFrascatiScaImporter
                    importer.setParentAppliImpl(appliImplModel);
                }
                if (serviceStackType != null) {
                    importer.setServiceStackType(serviceStackType);
                }
                if (serviceStackUrl != null) {
                    importer.setServiceStackUrl(serviceStackUrl);
                }
    
                importer.importSCA();
                navigationContext.navigateToRef(appliImplModel.getRef());
    
            } catch (Exception e) {
                log.error("Failed to import SCA", e);
            } finally {
                if (scaFile != null) {
                    scaFile.delete();
                }
            }
    
        }
    }

    private DocumentModelList getAllWorkspaces(CoreSession documentManager) throws ClientException {
        DocumentRef workspaceRoot = docService.getWorkspaceRoot(documentManager);
        return documentManager.getChildren(workspaceRoot, "Workspace", new DeletedDocumentFilter(), null);
    }

    private DocumentModelList getAllAppliImpls(CoreSession documentManager, DocumentRef workspace) throws ClientException {
        if (workspace != null) {
            return documentManager.getChildren(workspace, AppliImpl.DOCTYPE, new DeletedDocumentFilter(), null);
        }
        else {
            return new DocumentModelListImpl();
        }
    }

    private static List<SelectItem> modelsToSelectItems(DocumentModelList modelList) {
        // Transform into select item list
        List<SelectItem> items = new ArrayList<SelectItem>();
        for (DocumentModel model : modelList) {
            try {
                items.add(new SelectItem(model.getId(), model.getTitle()));
            } catch (Exception e) {
                log.warn("Failed to extract data from a document");
            }
        }
        return items;
    }

    public Blob getFile() {
        return file;
    }

    public void setFile(Blob file) {
        this.file = file;
    }

    public List<SelectItem> getWorkspaces() throws ClientException {
        return modelsToSelectItems(getAllWorkspaces(documentManager)); // TODO Caching
    }

    public List<SelectItem> getAppliImpls() throws ClientException {
        return modelsToSelectItems(getAllAppliImpls(documentManager, targetWorkspaceModel.getRef()));
    }

    public String getImportType() {
        return importType;
    }

    public void setImportType(String importType) {
        this.importType = importType;
    }

    public String getTargetWorkspace() {
        return targetWorkspace;
    }

    public void setTargetWorkspace(String id) throws ClientException {
        targetWorkspace = id;
        targetWorkspaceModel = documentManager.getDocument(new IdRef(id));
    }
    
    public void setTargetWorkspace(DocumentModel model) throws ClientException {
        targetWorkspace = model.getId();
        targetWorkspaceModel = model;
    }

    public String getTargetAppliImpl() {
        return targetAppliImpl;
    }

    public void setTargetAppliImpl(String targetAppliImpl) {
        this.targetAppliImpl = targetAppliImpl;
    }

}
