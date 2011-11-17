package org.easysoa.sca.visitors;

import org.easysoa.sca.IScaImporter;
import org.easysoa.services.DiscoveryService;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.runtime.api.Framework;

public class NxBindingVisitorFactory extends AbstractBindingVisitorFactoryBase {

	public NxBindingVisitorFactory(CoreSession documentManager){
		super(documentManager);
	}	
	
	@Override
	public ScaVisitor createReferenceBindingVisitor(IScaImporter scaImporter) {
		ScaVisitor scaVisitor = new ReferenceBindingVisitor(scaImporter, Framework.getRuntime().getService(DiscoveryService.class));
		scaVisitor.setDocumentManager(this.documentManager);
		return scaVisitor;
	}

	@Override
	public ScaVisitor createServiceBindingVisitor(IScaImporter scaImporter) {
		ScaVisitor scaVisitor = new ServiceBindingVisitor(scaImporter, Framework.getRuntime().getService(DiscoveryService.class));
		scaVisitor.setDocumentManager(this.documentManager);
		return scaVisitor;
	}

}
