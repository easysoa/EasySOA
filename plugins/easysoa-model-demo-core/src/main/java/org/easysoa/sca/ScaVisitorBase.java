package org.easysoa.sca;

import javax.xml.stream.XMLStreamReader;

import org.nuxeo.ecm.core.api.CoreSession;

/**
 * Visitor for REST reference bindings
 * Creates a new reference when a <binding.rest> tags is found.
 * @author mdutoo
 *
 */
// TODO: Refactor visitor implementations
public abstract class ScaVisitorBase implements ScaVisitor {

	protected ScaImporter scaImporter;
	protected CoreSession documentManager;
	protected XMLStreamReader compositeReader;
	
	public ScaVisitorBase(ScaImporter scaImporter) {
		this.scaImporter = scaImporter;
		this.documentManager = scaImporter.getDocumentManager();
		this.compositeReader = scaImporter.getCompositeReader();
	}

}
