package org.easysoa.sca.visitors;

import javax.xml.stream.XMLStreamReader;

import org.easysoa.sca.ScaImporter;
import org.easysoa.services.NotificationService;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.runtime.api.Framework;

/**
 * Visitor for REST reference bindings
 * Creates a new reference when a <binding.rest> tags is found.
 * @author mdutoo
 *
 */
// TODO: Refactor visitor implementations
public abstract class ScaVisitorBase implements ScaVisitor {

	protected CoreSession documentManager;
	protected ScaImporter scaImporter;
	protected XMLStreamReader compositeReader;
	protected NotificationService notificationService;
	
	public ScaVisitorBase(ScaImporter scaImporter) {
		this.documentManager = scaImporter.getDocumentManager();
		this.scaImporter = scaImporter;
		this.compositeReader = scaImporter.getCompositeReader();
		this.notificationService = Framework.getRuntime().getService(NotificationService.class);
	}
	
	@Override
	public String getDescription() {
		return this.toString();
	}

}
