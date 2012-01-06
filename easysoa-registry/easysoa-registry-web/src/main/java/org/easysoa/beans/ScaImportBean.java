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

import javax.faces.model.SelectItem;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.easysoa.doctypes.AppliImpl;
import org.easysoa.sca.IScaImporter;
import org.easysoa.sca.extension.ScaImporterComponent;
import org.easysoa.sca.visitors.BindingVisitorFactory;
import org.easysoa.sca.visitors.LocalBindingVisitorFactory;
import org.easysoa.services.DocumentService;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Create;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Install;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.nuxeo.ecm.core.api.Blob;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentModelList;
import org.nuxeo.ecm.core.api.IdRef;
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
public class ScaImportBean {

    private static Log log = LogFactory.getLog(ScaImportBean.class);

    @In(create = true, required = false)
    CoreSession documentManager;

    @In(create = true)
    NavigationContext navigationContext;

    List<SelectItem> appliImpls;

    private Blob compositeFile;

    private String parentAppliImpl;

    private String serviceStackType;

    private String serviceStackUrl;

    @Create
    public void init() throws ClientException {
        compositeFile = null;
        documentManager = navigationContext.getOrCreateDocumentManager();
        appliImpls = getAllAppliImplsAsSelectItems(documentManager);
        serviceStackType = "FraSCAti"; // TODO get it from wizard
        serviceStackUrl = "/"; // TODO get it from wizard
        // by choosing a stack (api server) type (frascati...)
        // (possibly initialized using composite info), then customizing it
    }

    /**
     * 
     */
    public void importSCA() {
        if (compositeFile != null) {
            
            File scaFile = null;

            try {
                // Transfer composite to temporary file
                if (compositeFile.getFilename().lastIndexOf(".") == -1) {
                    log.warn("Chosen file has no extension, file type might not be recognized.");
                }
                scaFile = File.createTempFile("scaimport", compositeFile.getFilename());
                compositeFile.transferTo(scaFile);

                BindingVisitorFactory visitorFactory = new LocalBindingVisitorFactory(documentManager);
                IScaImporter importer = Framework.getService(ScaImporterComponent.class).createScaImporter(visitorFactory, scaFile);

                DocumentModel appliImplModel = documentManager.getDocument(new IdRef(parentAppliImpl));
                if (parentAppliImpl != null) {
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

    public List<SelectItem> getAppliImpls() {
        return appliImpls;
    }

    public Blob getCompositeFile() {
        return compositeFile;
    }

    public void setCompositeFile(Blob compositeFile) {
        this.compositeFile = compositeFile;
    }

    public String getParentAppliImpl() {
        return parentAppliImpl;
    }

    public void setParentAppliImpl(String parentAppliImpl) {
        this.parentAppliImpl = parentAppliImpl;
    }

    private static List<SelectItem> getAllAppliImplsAsSelectItems(CoreSession documentManager) throws ClientException {

        // Gather information
        String wsRootId = null;
        try {
            DocumentService docService = Framework.getService(DocumentService.class);
            wsRootId = documentManager.getDocument(docService.getDefaultAppliImpl(documentManager).getParentRef()).getId();
        } catch (Exception e) {
            log.error("Failed to retrieve workspace root", e);
        }

        // Compute query
        String query = "SELECT * FROM " + AppliImpl.DOCTYPE + " WHERE ecm:currentLifeCycleState <> 'deleted' AND ecm:isProxy = 0";
        if (wsRootId != null) {
            query += " AND ecm:parentId = '" + wsRootId + "'";
        }

        // Send query
        List<SelectItem> appliImplItems = new ArrayList<SelectItem>();
        DocumentModelList appliImplList = documentManager.query(query);

        // Transform into wanted format
        for (DocumentModel appliImpl : appliImplList) {
            try {
                appliImplItems.add(new SelectItem(appliImpl.getId(), appliImpl.getTitle()));
            } catch (Exception e) {
                log.warn("Failed to extract data from an AppliImpl");
            }
        }
        return appliImplItems;
    }

}
