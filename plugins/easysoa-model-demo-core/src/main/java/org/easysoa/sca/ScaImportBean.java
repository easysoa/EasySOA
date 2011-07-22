package org.easysoa.sca;

import java.util.ArrayList;
import java.util.List;

import javax.faces.model.SelectItem;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.easysoa.doctypes.AppliImpl;
import org.easysoa.services.CacheHelper;
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

/**
 * SCA Import form
 * @author mkalam-alami
 *
 */
@Name("easysoaImport")
@Scope(ScopeType.CONVERSATION)
@Install(precedence = Install.FRAMEWORK)
public class ScaImportBean {

	private static final Log log = LogFactory.getLog(ScaImportBean.class);

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
        CacheHelper.invalidateVcsCache(); // FIXME: Doesn't work?
		compositeFile = null;
		documentManager = navigationContext.getOrCreateDocumentManager();
		appliImpls = getAllAppliImplsAsSelectItems(documentManager);
		serviceStackType = "FraSCAti"; // TODO get it from wizard
		serviceStackUrl = "/"; // TODO get it from wizard
		// by choosing a stack (api server) type (frascati...)
		// (possibly initialized using composite info), then customizing it
	}
	
	public void importSCA() {

		if (compositeFile == null)
			return;
		
		ScaImporter importer;
		try {
			importer = new ScaImporter(documentManager, compositeFile);
			if (parentAppliImpl != null) {
				importer.setParentAppliImpl(documentManager.getDocument(new IdRef(parentAppliImpl)));
			}
			if (serviceStackType != null) {
				importer.setServiceStackType(serviceStackType);
			}
			if (serviceStackUrl != null) {
				importer.setServiceStackUrl(serviceStackUrl);
			}
			importer.importSCA();
		} catch (Exception e) {
			log.error("Failed to import SCA", e);
		}
		
		navigationContext.goHome(); // XXX Doesn't work
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
		List<SelectItem> appliImplItems = new ArrayList<SelectItem>();
		DocumentModelList appliImplList = documentManager.query("SELECT * FROM " + AppliImpl.DOCTYPE
				+ " WHERE ecm:currentLifeCycleState <> 'deleted'");
		for (DocumentModel appliImpl : appliImplList) {
			try {
				appliImplItems.add(new SelectItem(appliImpl.getId(), appliImpl.getTitle()));
			}
			catch (Exception e) { 
				log.warn("Failed to extract data from an AppliImpl");
			}
		}
		return appliImplItems;
	}

}
