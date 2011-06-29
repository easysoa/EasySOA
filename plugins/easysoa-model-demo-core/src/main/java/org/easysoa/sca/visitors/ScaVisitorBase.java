package org.easysoa.sca.visitors;

import javax.xml.stream.XMLStreamReader;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.easysoa.sca.ScaImporter;
import org.nuxeo.ecm.core.api.CoreSession;

/**
 * Visitor for REST reference bindings
 * Creates a new reference when a <binding.rest> tags is found.
 * @author mdutoo
 *
 */
// TODO: Refactor visitor implementations
public abstract class ScaVisitorBase implements ScaVisitor {
	
	protected static final Log log = LogFactory.getLog(ScaImporter.class);

	protected ScaImporter scaImporter;
	protected CoreSession documentManager;
	protected XMLStreamReader compositeReader;
	
	public ScaVisitorBase(ScaImporter scaImporter) {
		this.scaImporter = scaImporter;
		this.documentManager = scaImporter.getDocumentManager();
		this.compositeReader = scaImporter.getCompositeReader();
	}
	
	@Override
	public String getDescription() {
		return this.toString();
	}

}
