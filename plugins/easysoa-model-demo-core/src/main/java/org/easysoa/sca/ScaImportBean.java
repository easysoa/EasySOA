package org.easysoa.sca;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.List;

import javax.faces.model.SelectItem;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.events.XMLEvent;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.easysoa.doctypes.AppliImpl;
import org.easysoa.services.DocumentService;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Create;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Install;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.nuxeo.common.utils.IdUtils;
import org.nuxeo.ecm.core.api.Blob;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentModelList;
import org.nuxeo.ecm.core.api.IdRef;
import org.nuxeo.ecm.platform.ui.web.api.NavigationContext;

@Name("easysoaImport")
@Scope(ScopeType.CONVERSATION)
@Install(precedence = Install.FRAMEWORK)
public class ScaImportBean {

	public static final String SCA_URI = "http://www.osoa.org/xmlns/sca/1.0";
	public static final String FRASCATI_URI = "http://frascati.ow2.org/xmlns/sca/1.1";
	public static final String WSDLINSTANCE_URI = "http://www.w3.org/2004/08/wsdl-instance";
	public static final QName SCA_SERVICE_QNAME = new QName(SCA_URI, "service");
	
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
	private DocumentModel appliImplModel;
	
	
	@Create
	public void init() throws ClientException {
		compositeFile = null;
		documentManager = navigationContext.getOrCreateDocumentManager();
		appliImpls = new ArrayList<SelectItem>();
		DocumentModelList appliImplList = documentManager.query("SELECT * FROM " + AppliImpl.DOCTYPE
				+ " WHERE ecm:currentLifeCycleState <> 'deleted'");
		for (DocumentModel appliImpl : appliImplList) {
			try {
				appliImpls.add(new SelectItem(appliImpl.getId(), appliImpl.getTitle()));
			}
			catch (Exception e) { 
				log.warn("Failed to extract data from an AppliImpl");
			}
		}
		serviceStackType = "FraSCAti"; // TODO get it from wizard
		serviceStackUrl = "/"; // TODO get it from wizard
		// by choosing a stack (api server) type (frascati...)
		// (possibly initialized using composite info), then customizing it
	}
	
	public List<ScaVisitor> createScaVisitors() {
		List<ScaVisitor> visitors = new ArrayList<ScaVisitor>();
		visitors.add(new WSBindingScaVisitor(documentManager, appliImplModel, serviceStackType, serviceStackUrl));
		visitors.add(new RestBindingScaVisitor(documentManager, appliImplModel, serviceStackType, serviceStackUrl));
		return visitors;
	}

	public void importSCA() throws Exception {

		if (compositeFile == null)
			return;
		
		// Initialization
		if (parentAppliImpl == null)
			appliImplModel = DocumentService.getDefaultAppliImpl(documentManager);
		else
			appliImplModel = documentManager.getDocument(new IdRef(parentAppliImpl));
		File compositeTmpFile = File.createTempFile(IdUtils.generateStringId(), ".composite");
		compositeFile.transferTo(compositeTmpFile);
		
		// (using StAX parsing to get both sax & dom parsing benefits)
		XMLInputFactory xmlif = XMLInputFactory.newInstance();
		XMLStreamReader compositeReader = xmlif.createXMLStreamReader(new FileInputStream(compositeTmpFile));
		
		while(compositeReader.hasNext()) {
			if (compositeReader.next() == XMLEvent.START_ELEMENT
					&& compositeReader.getName().equals(SCA_SERVICE_QNAME)) {
				// service !
				String serviceName = compositeReader.getAttributeValue("", "name"); // TODO SCA_URI
				
				while (compositeReader.next() != XMLEvent.END_ELEMENT
						|| !compositeReader.getName().equals(SCA_SERVICE_QNAME)) {
					if (compositeReader.getEventType() == XMLEvent.START_ELEMENT
							&& compositeReader.getLocalName().startsWith("binding.")) {
						// binding !						
						
						acceptBindingVisitors(compositeReader, serviceName);
					}
				}
			}
		}

		documentManager.save();
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

	public static String getApiUrl(String serviceUrl, String appliImplUrl, String serviceStackUrl) {
		String apiPath = serviceStackUrl; // TODO  + "/" + serviceStackUrl + "/"
		if (appliImplUrl != null) {
			apiPath = appliImplUrl + apiPath;
		}
		
		int apiPathIndex = serviceUrl.indexOf(apiPath);
		if (apiPathIndex == -1) {
			apiPathIndex = serviceUrl.indexOf('/');
		}
		return serviceUrl.substring(0, apiPathIndex + apiPath.length());
	}

	private void acceptBindingVisitors(XMLStreamReader compositeReader, String serviceName) throws ClientException {
		QName bindingQName = compositeReader.getName();
		
		for (ScaVisitor scaVisitor : createScaVisitors()) {
			if (scaVisitor.isOkFor(bindingQName)) {
				scaVisitor.visit(compositeReader, serviceName);
				// TODO for now, whether it visits children is done in handle()
			}
		}
	}
	

}
