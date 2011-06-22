package org.easysoa.sca;

import org.easysoa.doctypes.EasySOADoctype;
import org.easysoa.doctypes.ServiceReference;
import org.easysoa.services.DocumentService;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.DocumentModel;

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
	
	public void visit() throws ClientException {
		
		// find existing reference
		String referenceArchiPath = scaImporter.toCurrentArchiPath();
		referenceModel = DocumentService.findReference(documentManager, referenceArchiPath);
		if (referenceModel != null){
			// TODO handle enriching / merge of service or even api
			return;
		}

		// getting referenced service url
		String refUrl = getBindingUrl();
		
		// create reference
		referenceModel = DocumentService.createReference(documentManager,
				scaImporter.getParentAppliImplModel().getPathAsString(), referenceArchiPath);
		referenceModel.setProperty(ServiceReference.SCHEMA, ServiceReference.PROP_REFURL, refUrl);
		referenceModel.setProperty(EasySOADoctype.SCHEMA_COMMON, EasySOADoctype.PROP_ARCHIPATH, referenceArchiPath);
		referenceModel.setProperty(EasySOADoctype.SCHEMA_COMMON, EasySOADoctype.PROP_ARCHILOCALNAME, scaImporter.getCurrentArchiName());
		referenceModel.setProperty(EasySOADoctype.SCHEMA_COMMON, EasySOADoctype.PROP_DTIMPORT,
				scaImporter.getCompositeFile().getFilename()); // TODO also upload and link to it ??
		documentManager.saveDocument(referenceModel);
	}
	
	@Override
	public void postCheck() throws ClientException {
		// find referenced service
		String refUrl = (String) referenceModel.getProperty(ServiceReference.SCHEMA, ServiceReference.PROP_REFURL);
		DocumentModel refServiceModel = DocumentService.findService(documentManager, refUrl);
		if (refServiceModel != null) {
			referenceModel.setProperty(ServiceReference.SCHEMA, ServiceReference.PROP_REFPATH, refServiceModel.getPathAsString());
			documentManager.saveDocument(referenceModel);
		}
	}

	protected abstract String getBindingUrl();

}
