package org.easysoa.treestructure;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.PathRef;
import org.nuxeo.ecm.core.event.Event;
import org.nuxeo.ecm.core.event.EventContext;
import org.nuxeo.ecm.core.event.EventListener;
import org.nuxeo.ecm.core.event.impl.DocumentEventContext;
import org.nuxeo.runtime.api.Framework;

/**
 * Moves documents on creation to the correct folders.
 * 
 * @author mkalam-alami
 * 
 */
public class MoveDocumentsListeners implements EventListener {
	
	private static final Log log = LogFactory.getLog(MoveDocumentsListeners.class);
	private static final String SERVICE_TYPE = "Service";

	public void handleEvent(Event event) throws ClientException {
		
		// Check event type
		EventContext ctx = event.getContext();
		if (!(ctx instanceof DocumentEventContext)) {
			return;
		}

		// Check document type
		List<String> descriptorTypes = null;
		try {
			WorkspaceDeployerComponent wsDeployer = (WorkspaceDeployerComponent) Framework
					.getRuntime().getComponent("org.easysoa.component.workspacedeployer");
			descriptorTypes = wsDeployer.getDescriptorTypes();
		} catch (Exception e) {
			log.error("Cannot get Descriptor Types");
			return;
		}
		DocumentModel doc = ((DocumentEventContext) ctx).getSourceDocument();
		if (doc == null) {
			return;
		}
		String type = doc.getType();
		if ((!descriptorTypes.contains(type)) && (!SERVICE_TYPE.equals(type))) {
			return;
		}

		CoreSession session = ctx.getCoreSession();

		// Move document
		if (descriptorTypes.contains(type)) {
			log.info("Moved descriptor : " + doc.getPathAsString());
			session.move(doc.getRef(), new PathRef(
					WorkspaceDeployer.DESCRIPTORS_WORKSPACE + doc.getType()),
					doc.getTitle());
		} else if (!SERVICE_TYPE.equals(type)) {
			log.info("Moved service : " + doc.getPathAsString());
			session.move(doc.getRef(), new PathRef(
					WorkspaceDeployer.SERVICES_WORKSPACE + doc.getType()),
					doc.getTitle());
		}
	}
}