package org.easysoa.beans;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.faces.model.SelectItem;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.easysoa.services.DocumentService;
import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Create;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Install;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.contexts.Contexts;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.Namespace;
import org.jdom.input.SAXBuilder;
import org.nuxeo.common.utils.IdUtils;
import org.nuxeo.ecm.core.api.Blob;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentModelList;
import org.nuxeo.ecm.core.api.IdRef;
import org.nuxeo.ecm.platform.ui.web.api.NavigationContext;
import org.nuxeo.ecm.webapp.context.ServerContextBean;
import org.nuxeo.ecm.webapp.delegate.DocumentManagerBusinessDelegate;

@Name("easysoaImport")
@Scope(ScopeType.CONVERSATION)
@Install(precedence = Install.FRAMEWORK)
public class ImportBean {

	private static final Log log = LogFactory.getLog(ImportBean.class);

	CoreSession documentManager;

	@In(create = true)
	NavigationContext navigationContext;

	List<SelectItem> appliImpls;

	private Blob compositeFile;

	private String parentAppliImpl;
	
	@Create
	public void init() throws ClientException {
		compositeFile = null;
		documentManager = getOrCreateDocumentManager();
		appliImpls = new ArrayList<SelectItem>();
		DocumentModelList appliImplList = documentManager.query("SELECT * FROM Workspace WHERE ecm:currentLifeCycleState <> 'deleted'");
		for (DocumentModel appliImpl : appliImplList) {
			try {
				appliImpls.add(new SelectItem(appliImpl.getId(), appliImpl.getTitle()));
			}
			catch (Exception e) { 
				log.warn("Failed to extract data from an AppliImpl");
			}
		}
	}

	public void importSCA() throws Exception {
		

		// Initialization
		File tmpFile = File.createTempFile(IdUtils.generateStringId(),
				".composite");
		compositeFile.transferTo(tmpFile);
		SAXBuilder builder = new SAXBuilder();
		Document document = builder.build(tmpFile);
		Element root = document.getRootElement();
		Namespace namespace = root.getNamespace();

		String title = root.getAttributeValue("name");
		String url = title; // TODO
		DocumentModelList apiChildren = null;

		// Root document
		DocumentModel apiModel = DocumentService.findServiceApi(documentManager, url);
		if (apiModel == null) {
			apiModel = DocumentService.createServiceAPI(documentManager, url, title);
			apiModel.setProperty("serviceapidef", "url", url);
			documentManager.saveDocument(apiModel);
			documentManager.save();
		}
		else {
			apiChildren = documentManager.getChildren(apiModel.getRef());
		}
		documentManager.move(apiModel.getRef(), new IdRef(parentAppliImpl), apiModel.getName());
		
		// Look for services
		for (Object child : root.getChildren()) {
			if (child instanceof Element) {
				Element element = (Element) child;
				if (element.getName().equals("component")) {
					for (Object child2 : element.getChildren("service", namespace)) {
						if (child2 instanceof Element) {
							
							// Create found service if it doesn't exist already
							boolean serviceExists = false;
							String serviceTitle = ((Element) child2).getAttributeValue("name");
							if (apiChildren != null) {
								for (DocumentModel model : apiChildren) {
									if (model.getTitle().equals(serviceTitle)) {
										serviceExists = true;
										break;
									}
								}
							}
							if (!serviceExists)
								DocumentService.createService(documentManager, url, serviceTitle);
						}
					}

				}
			}

		}

		documentManager.save();
		navigationContext.goHome();
	}

	
    /**
     * Taken from org.nuxeo.ecm.webapp.context.NavigationContextBean
     * 
     * Returns the current documentManager if any or create a new session to
     * the current location.
     */
    public CoreSession getOrCreateDocumentManager() throws ClientException {
        if (documentManager != null) {
            return documentManager;
        }
        // protect for unexpected wrong cast
        Object supposedDocumentManager = Contexts.lookupInStatefulContexts("documentManager");
        DocumentManagerBusinessDelegate documentManagerBD = null;
        if (supposedDocumentManager != null) {
            if (supposedDocumentManager instanceof DocumentManagerBusinessDelegate) {
                documentManagerBD = (DocumentManagerBusinessDelegate) supposedDocumentManager;
            } else {
                log.error("Found the documentManager being "
                        + supposedDocumentManager.getClass()
                        + " instead of DocumentManagerBusinessDelegate. This is wrong.");
            }
        }
        if (documentManagerBD == null) {
            // this is the first time we select the location, create a
            // DocumentManagerBusinessDelegate instance
            documentManagerBD = new DocumentManagerBusinessDelegate();
            Contexts.getConversationContext().set("documentManager",
                    documentManagerBD);
        }
        documentManager = documentManagerBD.getDocumentManager(
        		((ServerContextBean) Component.getInstance("serverLocator")).getCurrentServerLocation());
        return documentManager;
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

	public List<SelectItem> getAppliImpls() {
		return appliImpls;
	}
	

}
