package org.easysoa.sca.visitors;

import org.easysoa.doctypes.EasySOADoctype;
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
		String referenceArchiPath = scaImporter.toCurrentArchiPath();

		// getting referenced service url
		String refUrl = getBindingUrl();
		
		// find reference, then enrich or create
		DocumentService docService = Framework.getRuntime().getService(DocumentService.class); 
		referenceModel = docService.findReference(documentManager, referenceArchiPath);
		if (referenceModel == null){
			referenceModel = docService.createReference(documentManager,
					scaImporter.getParentAppliImplModel().getPathAsString(), referenceArchiPath);
		}
		referenceModel.setProperty(ServiceReference.SCHEMA, ServiceReference.PROP_REFURL, refUrl);
		referenceModel.setProperty(EasySOADoctype.SCHEMA_COMMON, EasySOADoctype.PROP_ARCHIPATH, referenceArchiPath);
		referenceModel.setProperty(EasySOADoctype.SCHEMA_COMMON, EasySOADoctype.PROP_ARCHILOCALNAME, scaImporter.getCurrentArchiName());
		referenceModel.setProperty(EasySOADoctype.SCHEMA_COMMON, EasySOADoctype.PROP_DTIMPORT,
				scaImporter.getCompositeFile().getFilename()); // TODO also upload and link to it ??
		documentManager.saveDocument(referenceModel);
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
