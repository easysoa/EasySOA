package org.easysoa.sca.visitors;

import java.net.MalformedURLException;

import javax.xml.namespace.QName;

import org.easysoa.doctypes.AppliImpl;
import org.easysoa.doctypes.EasySOADoctype;
import org.easysoa.sca.ScaImporter;
import org.easysoa.services.DocumentService;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.runtime.api.Framework;

/**
 * Visitor for WS bindings.
 * Creates new services when <binding.ws> tags are found.
 * @author mkalam-alami
 *
 */
// TODO: Refactor visitor implementations
public abstract class ServiceBindingVisitorBase extends ScaVisitorBase {
	
	public ServiceBindingVisitorBase(ScaImporter scaImporter) {
		super(scaImporter);
	}

	public boolean isOkFor(QName bindingQName) {
		return bindingQName.equals(new QName(ScaImporter.SCA_URI, "binding.ws"));
	}
	
	public void visit() throws ClientException, MalformedURLException {
		
		String serviceUrl = getBindingUrl();
		if (serviceUrl == null) {
			// TODO error
			return;
		}
		
		String appliImplUrl = (String) scaImporter.getParentAppliImplModel().getProperty(AppliImpl.SCHEMA, AppliImpl.PROP_URL);
		String appliImplPath = scaImporter.getParentAppliImplModel().getPathAsString();
		String apiUrl = ScaImporter.getApiUrl(appliImplUrl, scaImporter.getServiceStackUrl(), serviceUrl);
		String apiName = scaImporter.getServiceStackType(); // TODO better, ex. from composite name...

		DocumentService docService = Framework.getRuntime().getService(DocumentService.class); 
		
		// find api, then enrich or create
		DocumentModel apiModel = docService.findServiceApi(documentManager, apiUrl);
		if (apiModel == null) {	// assuming it is the parent TODO tree :
			apiModel = docService.createServiceAPI(documentManager, appliImplPath, apiUrl);
		}
		apiModel.setProperty("dublincore", "title", apiName);
		apiModel.setProperty(EasySOADoctype.SCHEMA_COMMON, EasySOADoctype.PROP_DTIMPORT, scaImporter.getCompositeFile().getFilename());
		apiModel = documentManager.saveDocument(apiModel);
		documentManager.save(); // Save all so that the newly created API can be found by the DocumentService
		
		// find service, then enrich or create
		DocumentModel serviceModel = docService.findService(documentManager, serviceUrl);
		if (serviceModel == null){
			serviceModel = docService.createService(documentManager, apiModel.getPathAsString(), serviceUrl);
		}
		serviceModel.setProperty("dublincore", "title", scaImporter.getCurrentArchiName());
		serviceModel.setProperty(EasySOADoctype.SCHEMA_COMMON, EasySOADoctype.PROP_ARCHIPATH, scaImporter.toCurrentArchiPath());
		serviceModel.setProperty(EasySOADoctype.SCHEMA_COMMON, EasySOADoctype.PROP_ARCHILOCALNAME, scaImporter.getCurrentArchiName());
		serviceModel.setProperty(EasySOADoctype.SCHEMA_COMMON, EasySOADoctype.PROP_DTIMPORT,
				scaImporter.getCompositeFile().getFilename()); // TODO also upload and link to it ??
		documentManager.saveDocument(serviceModel);
		
	}

	protected abstract String getBindingUrl();

	@Override
	public void postCheck() throws ClientException {
		// nothing to do
	}

}
