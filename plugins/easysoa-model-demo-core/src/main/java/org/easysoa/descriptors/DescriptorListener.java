package org.easysoa.descriptors;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.easysoa.tools.RelationService;
import org.easysoa.tools.VocabularyService;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.IdRef;
import org.nuxeo.ecm.core.event.Event;
import org.nuxeo.ecm.core.event.EventContext;
import org.nuxeo.ecm.core.event.EventListener;
import org.nuxeo.ecm.core.event.impl.DocumentEventContext;

/**
 * Entry point for service descriptors handling (creation/modification).
 * Manages descriptor list vocabulary, and allows doctype-specific classes to do further processing.
 * 
 * @author mkalam-alami
 *
 */
public class DescriptorListener implements EventListener {
	
	private static final Log log = LogFactory.getLog(DescriptorListener.class);
	private static final String DESCRIPTOR_DOCTYPE = "ServiceDescriptor";
	private static final String DESCRIPTOR_VOCABULARY = "descriptorlist";

	public void handleEvent(Event event) {
		
		// Check event type
		EventContext ctx = event.getContext();
		if (!(ctx instanceof DocumentEventContext)) {
			return;
		}
		
		CoreSession session = ctx.getCoreSession();
		DocumentModel doc = ((DocumentEventContext) ctx).getSourceDocument();

		// Document removal
		log.info(event.getName());
		if (event.getName().equals("documentRemoved")) {
			VocabularyService.removeEntry(session, DESCRIPTOR_VOCABULARY, doc.getId());
			return;
		}
		
		// Check document type + Document-type specific processing
		if (doc == null) {
			return;
		}
		String type = doc.getType();
		if (type.equals(DESCRIPTOR_DOCTYPE)) {
			// Nothing
		}
		else if (type.equals(WSDLService.WSDL_DOCTYPE)) {
			WSDLService.update(session, doc);
		}
		else {
			return;
		}
		
		// Manage relation with service
		// TODO: put endpoints:serviceid in a Service Descriptor schema
		try {
			
			DocumentModel savedDoc = session.getDocument(doc.getRef());
			String savedServiceId = (String) savedDoc.getProperty("endpoints", "serviceid"),
					newServiceId = (String) doc.getProperty("endpoints", "serviceid");
			
			log.info("DESC: "+savedServiceId + "  vs " + doc.getProperty("endpoints", "serviceid"));

			if (savedServiceId == null ||
					!savedServiceId.equals(doc.getProperty("endpoints", "serviceid"))) {
				
				log.info("Descriptor's service modified, updating relations.");
				
				// Clear old relation
				RelationService.clearRelations(doc);
				if (savedServiceId != null && !savedServiceId.equals("")) {
					DocumentModel savedService = session.getDocument(new IdRef(savedServiceId));
					if (savedService != null) {
						String oldServiceDescId = (String) savedService.getProperty("serviceTags", "descriptorid");
						if (oldServiceDescId != null && !oldServiceDescId.isEmpty()) {
							RelationService.clearRelations(savedService);
							savedService.setProperty("serviceTags", "descriptorid", "");
							log.info("Old service cleared.");
							// TODO: Save without saving loop...
						}
					}
					else {
						log.warn("Old descriptor document of ID "+savedServiceId+" not found.");
					}
				}

				// Create new relation
				if (newServiceId != null && !newServiceId.isEmpty()) {
					DocumentModel newService = session.getDocument(new IdRef(newServiceId));
					if (newService != null) {
						RelationService.createRelation(newService, doc);
						String newServiceDescId = (String) newService.getProperty("serviceTags", "descriptorid");
						if (newServiceDescId == null || !newServiceDescId.equals(doc.getId())) {
							newService.setProperty("serviceTags", "descriptorid", doc.getId());
							log.info("New service modified.");
							// TODO: Save without saving loop...
						}
					}
					else {
						log.error("Descriptor document of ID "+newServiceId+" not found.");
					}
				}
			}

		} catch (Exception e) {
			log.error("Error while updating relations", e);
		}

		// Descriptor list vocaulary management
		try {
			if (!VocabularyService.entryExists(session, DESCRIPTOR_VOCABULARY, doc.getId()))
				VocabularyService.addEntry(session, DESCRIPTOR_VOCABULARY, doc.getId(), doc.getTitle());
		} catch (Exception e) {
			log.error("Error while updating vocabulary", e);
		}

		// Save
		try {
			session.save();
		} catch (Exception e) {
			log.error("Error while saving descriptor", e);
		}
	}
}