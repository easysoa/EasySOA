package org.easysoa.sca.visitors;

import java.util.HashMap;
import java.util.Map;

import org.easysoa.doctypes.AppliImpl;
import org.easysoa.doctypes.ServiceReference;
import org.easysoa.sca.ScaImporter;
import org.easysoa.services.DocumentService;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.runtime.api.Framework;

/**
 * Visitor for REST reference bindings
 * Creates a new reference when a <binding.rest> tags is found.
 * @author mdutoo
 *
 */
// TODO: Refactor visitor implementations
public abstract class ReferenceBindingVisitorBase extends ScaVisitorBase {
	
	protected DocumentModel referenceModel;

	public ReferenceBindingVisitorBase(ScaImporter scaImporter) {
		super(scaImporter);
	}
	
	@Override
	public String getDescription() {
		StringBuffer sbuf = new StringBuffer(this.toString());
		sbuf.append("[path=");
		sbuf.append(referenceModel.getPathAsString());
		sbuf.append(",type=");
		sbuf.append(referenceModel.getType());
		sbuf.append(",title=");
		try {
			sbuf.append(referenceModel.getPropertyValue("dc:title"));
		} catch (Exception ex) {
			String msg = "error while getting title";
			sbuf.append("(" + msg + ")");
			log.error(msg, ex);
		}
		sbuf.append("]");
		return sbuf.toString();
	}
	
	public void visit() throws ClientException {

		Map<String, String> properties = new HashMap<String, String>();
		properties.put(ServiceReference.PROP_REFURL, getBindingUrl()); // getting referenced service url
		properties.put(ServiceReference.PROP_ARCHIPATH, scaImporter.toCurrentArchiPath());
		properties.put(ServiceReference.PROP_ARCHILOCALNAME, scaImporter.getCurrentArchiName());
		properties.put(ServiceReference.PROP_DTIMPORT, scaImporter.getCompositeFile().getFilename()); // TODO also upload and link to it ??
		properties.put(ServiceReference.PROP_PARENTURL, 
				(String) scaImporter.getParentAppliImplModel().getProperty(AppliImpl.SCHEMA, AppliImpl.PROP_URL));
		referenceModel = notificationService.notifyServiceReference(documentManager, properties);
		
	}
	
	@Override
	public void postCheck() throws ClientException {
		
		DocumentService docService = Framework.getRuntime().getService(DocumentService.class); 
		// find referenced service
		String refUrl = (String) referenceModel.getProperty(ServiceReference.SCHEMA, ServiceReference.PROP_REFURL);
		DocumentModel refServiceModel = docService.findService(documentManager, refUrl);
		if (refServiceModel != null) {
			referenceModel.setProperty(ServiceReference.SCHEMA, ServiceReference.PROP_REFPATH, refServiceModel.getPathAsString());
			documentManager.saveDocument(referenceModel);
		}
	}

	protected abstract String getBindingUrl();

}
