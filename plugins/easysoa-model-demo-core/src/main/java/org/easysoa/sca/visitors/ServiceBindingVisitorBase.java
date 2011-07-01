package org.easysoa.sca.visitors;

import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.Map;

import javax.xml.namespace.QName;

import org.easysoa.doctypes.AppliImpl;
import org.easysoa.doctypes.Service;
import org.easysoa.doctypes.ServiceAPI;
import org.easysoa.sca.ScaImporter;
import org.nuxeo.ecm.core.api.ClientException;

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
		String apiUrl = notificationService.computeApiUrl(appliImplUrl, scaImporter.getServiceStackUrl(), serviceUrl);
		
		// enrich or create API
		Map<String, String> properties = new HashMap<String, String>();
		properties.put(ServiceAPI.PROP_URL, apiUrl);
		properties.put(ServiceAPI.PROP_TITLE, scaImporter.getServiceStackType()); // TODO better, ex. from composite name...
		properties.put(ServiceAPI.PROP_DTIMPORT, scaImporter.getCompositeFile().getFilename());
		properties.put(ServiceAPI.PROP_PARENTURL, appliImplUrl);
		notificationService.notifyApi(documentManager, properties);

		// enrich or create service
		properties = new HashMap<String, String>();
		properties.put(Service.PROP_URL, serviceUrl);
		properties.put(Service.PROP_PARENTURL, apiUrl);
		properties.put(Service.PROP_ARCHIPATH, scaImporter.toCurrentArchiPath());
		properties.put(Service.PROP_ARCHILOCALNAME, scaImporter.getCurrentArchiName());
		properties.put(Service.PROP_DTIMPORT, scaImporter.getCompositeFile().getFilename()); // TODO also upload and link to it ?
		notificationService.notifyService(documentManager, properties);
		
	}

	protected abstract String getBindingUrl();

	@Override
	public void postCheck() throws ClientException {
		// nothing to do
	}

}
