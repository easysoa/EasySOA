package org.easysoa.sca;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamReader;

import org.easysoa.doctypes.AppliImpl;
import org.easysoa.services.DocumentService;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;

/**
 * Visitor for REST bindings.
 * Creates new services when <binding.ws> tags are found.
 * @author mkalam-alami
 *
 */
// TODO: Refactor visitor implementations
public class WSBindingScaVisitor implements ScaVisitor {

	private CoreSession documentManager;
	private DocumentModel appliImplModel;
	private String serviceStackType;
	private String serviceStackUrl;
	
	public WSBindingScaVisitor(CoreSession documentManager, DocumentModel appliImplModel,
			String serviceStackType, String serviceStackUrl) {
		this.documentManager = documentManager;
		this.appliImplModel = appliImplModel;
		this.serviceStackType = serviceStackType;
		this.serviceStackUrl = serviceStackUrl;
	}
	
	public boolean isOkFor(QName bindingQName) {
		return bindingQName.equals(new QName(ScaImportBean.SCA_URI, "binding.ws"));
	}
	
	public void visit(XMLStreamReader compositeReader, String serviceName) throws ClientException {
		
		String serviceUrl = compositeReader.getAttributeValue("", "uri");
		if (serviceUrl == null) {
			String wsdlLocation = compositeReader.getAttributeValue(ScaImportBean.WSDLINSTANCE_URI , "wsdlLocation");
			if (wsdlLocation != null) {
				serviceUrl = wsdlLocation.replace("?wsdl", "");
			}
		}
		
		DocumentModel serviceModel = DocumentService.findService(documentManager, serviceUrl);
		if (serviceModel != null){
			// TODO handle enriching / merge of service or even api
			return;
		}
		
		// create api
		String appliImplUrl = (String) appliImplModel.getProperty(AppliImpl.SCHEMA, AppliImpl.PROP_URL);
		String appliImplPath = appliImplModel.getPathAsString();
		String apiUrl = ScaImportBean.getApiUrl(serviceUrl, appliImplUrl, serviceStackUrl);
		String apiName = serviceStackType; // TODO better, ex. from composite name...
		
		DocumentModel apiModel = DocumentService.findServiceApi(documentManager, apiUrl);
		if (apiModel == null) {	// assuming it is the parent TODO tree :
			apiModel = DocumentService.createServiceAPI(documentManager, appliImplPath, apiUrl);
			apiModel.setProperty("dublincore", "title", apiName);
			apiModel = documentManager.saveDocument(apiModel);
			documentManager.save(); // Save all so that the newly created API can be found by the DocumentService
		}
		
		// create service
		serviceModel = DocumentService.createService(documentManager, apiModel.getPathAsString(), serviceUrl);
		serviceModel.setProperty("dublincore", "title", serviceName);
		documentManager.saveDocument(serviceModel);
		
	}

}
