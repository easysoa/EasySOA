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
import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Create;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Install;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.contexts.Contexts;
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
public class ScaImportBean {

	public static final String SCA_URI = "http://www.osoa.org/xmlns/sca/1.0";
	public static final String FRASCATI_URI = "http://frascati.ow2.org/xmlns/sca/1.1";
	public static final String WSDLINSTANCE_URI = "http://www.w3.org/2004/08/wsdl-instance";
	public static final QName SCA_SERVICE_QNAME = new QName(SCA_URI, "service");
	
	private static final Log log = LogFactory.getLog(ScaImportBean.class);
	
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
		documentManager = getOrCreateDocumentManager();
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
		return new ArrayList<ScaVisitor>() {
			
			private static final long serialVersionUID = 1L;

			{
				
				add(new ScaVisitor() {
					/* BindingWsServiceCreatorScaVisitor */

					public boolean isOkFor(QName bindingQName) {
						return bindingQName.equals(new QName(SCA_URI, "binding.ws"));
					}
					
					public void visit(XMLStreamReader compositeReader, String serviceNameString) throws ClientException {
						String serviceUrl = compositeReader.getAttributeValue("", "uri");
						if (serviceUrl == null) {
							String wsdlLocation = compositeReader.getAttributeValue(WSDLINSTANCE_URI , "wsdlLocation");
							if (wsdlLocation != null) {
								serviceUrl = wsdlLocation.replace("?wsdl", "");
							}
						}
						
						DocumentModel serviceModel = DocumentService.findService(ScaImportBean.this.documentManager, serviceUrl);
						if (serviceModel != null){
							// TODO handle enriching / merge of service or even api
							return;
						}
						
						// create api
						String apiImplUrl = (String) ScaImportBean.this.appliImplModel.getProperty(AppliImpl.SCHEMA, AppliImpl.PROP_URL);
						String appliImplPath = ScaImportBean.this.appliImplModel.getPathAsString();
						String apiUrl = ScaImportBean.this.getApiUrl(serviceUrl, apiImplUrl, ScaImportBean.this.serviceStackUrl);
						String apiName = ScaImportBean.this.serviceStackType; // TODO better, ex. from composite name...
						
						DocumentModel apiModel = DocumentService.findServiceApi(documentManager, apiUrl);
						if (apiModel == null)
							apiModel = ScaImportBean.this.createApiOfService(appliImplPath, apiUrl, apiName);
						
						// create service
						DocumentService.createService(ScaImportBean.this.documentManager, apiModel.getPathAsString(), serviceNameString);
					}
	
					
				});
				
	
				add(new ScaVisitor() {
					/* BindingRestServiceCreatorScaVisitor */
	
					public boolean isOkFor(QName bindingQName) {
						return bindingQName.equals(new QName(FRASCATI_URI, "binding.rest"));
					}
					
					public void visit(XMLStreamReader compositeReader, String serviceNameString) throws ClientException {
						String serviceUrl = compositeReader.getAttributeValue(FRASCATI_URI, "uri");
						
						if (serviceUrl != null) {
							DocumentModel serviceModel = DocumentService.findService(ScaImportBean.this.documentManager, serviceUrl);
							if (serviceModel != null){
								// TODO handle enriching / merge of service or even api
								return;
							}
							
							// create api
							String apiUrl = ScaImportBean.this.getApiUrl(serviceUrl, ScaImportBean.this.appliImplModel.getPathAsString(),
									ScaImportBean.this.serviceStackUrl);
							String appliImplPath = ScaImportBean.this.appliImplModel.getPathAsString();
							String apiName = ScaImportBean.this.serviceStackType; // TODO better, ex. from composite name...
							
							DocumentModel apiModel = DocumentService.findServiceApi(documentManager, apiUrl);
							if (apiModel == null)
								apiModel = ScaImportBean.this.createApiOfService(appliImplPath, apiUrl, apiName);
							
							// create service
							DocumentService.createService(ScaImportBean.this.documentManager, apiModel.getPathAsString(), serviceNameString);
						}
					}
	
					
				});
			}
		};

	}
	

	public void importSCA() throws Exception {
		// Initialization
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
				String serviceNameString = compositeReader.getAttributeValue("", "name"); // TODO SCA_URI

				while (compositeReader.next() != XMLEvent.END_ELEMENT
						|| !compositeReader.getName().equals(SCA_SERVICE_QNAME)) {
					if (compositeReader.getEventType() == XMLEvent.START_ELEMENT
							&& compositeReader.getLocalName().startsWith("binding.")) {
						// binding !						
						
						acceptBindingVisitors(compositeReader, serviceNameString);
					}
				}
			}
		}

		documentManager.save();
		navigationContext.goHome();
	}

	
	private void acceptBindingVisitors(XMLStreamReader compositeReader, String serviceNameString) throws ClientException {
		QName bindingQName = compositeReader.getName();
		
		for (ScaVisitor scaVisitor : createScaVisitors()) {
			if (scaVisitor.isOkFor(bindingQName)) {
				scaVisitor.visit(compositeReader, serviceNameString);
				// TODO for now, whether it visits children is done in handle()
			}
		}
	}

	
	
	// TODO move e.g. in DocumentService
	/*
	private DocumentModel getApiOfService(String serviceUrl) throws ClientException {
		DocumentModel serviceModel = DocumentService.findService(documentManager, serviceUrl);
		if (serviceModel == null){
			return null;	
		}
		return documentManager.getDocument(serviceModel.getParentRef());
	}*/
	
	private DocumentModel createApiOfService(String appliImplPath, String apiUrl, String apiName) throws ClientException {
		// assuming it is the parent TODO tree :
		DocumentModel apiModel = DocumentService.createServiceAPI(documentManager, appliImplPath, apiUrl);
		apiModel.setProperty("dublincore", "title", apiName);
		documentManager.saveDocument(apiModel);
		documentManager.save(); // Save all so that the newly created API can be found by the DocumentService
		return apiModel;
	}

	private String getApiUrl(String serviceUrl, String appliImplUrl, String serviceStackUrl) {
		String apiPath = appliImplUrl + serviceStackUrl; // TODO  + "/" + serviceStackUrl + "/"
		int apiPathIndex = serviceUrl.indexOf(apiPath);
		if (apiPathIndex != -1) {
			return serviceUrl.substring(0, apiPathIndex + apiPath.length());
		}
		return null;
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
