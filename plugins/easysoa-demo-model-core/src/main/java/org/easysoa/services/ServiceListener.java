package org.easysoa.services;

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
 * Entry point for services handling (creation/modification).
 * 
 * @author mkalam-alami
 *
 */
public class ServiceListener implements EventListener {
	
	public static final String SERVICE_DOCTYPE = "Service";
	public static final String SERVICE_VOCABULARY = "servicelist";
	
	private static final Log log = LogFactory.getLog(ServiceListener.class);

	public void handleEvent(Event event) {
		
		// Check event type
		EventContext ctx = event.getContext();
		if (!(ctx instanceof DocumentEventContext)) {
			return;
		}

		// Check document type
		DocumentModel doc = ((DocumentEventContext) ctx).getSourceDocument();
		if (doc == null || !doc.getType().equals(SERVICE_DOCTYPE) ) {
			return;
		}

		CoreSession session = ctx.getCoreSession();
		
		// Document removal
		log.info(event.getName());
		if (event.getName().equals("documentRemoved")) {
			VocabularyService.removeEntry(session, SERVICE_VOCABULARY, doc.getId());
			return;
		}
		
		// Service list vocabulary management
		try {
			if (!VocabularyService.entryExists(session, SERVICE_VOCABULARY, doc.getId()))
				VocabularyService.addEntry(session, SERVICE_VOCABULARY, doc.getId(), doc.getTitle());
		} catch (Exception e) {
			log.error("Error while updating vocabulary", e);
		}

		// Manage relation with descriptor
		try {

			DocumentModel savedDoc = session.getDocument(doc.getRef());
			String savedDescId = (String) savedDoc.getProperty("serviceTags", "descriptorid");

			log.info("SERV: "+savedDescId + "  vs " + doc.getProperty("serviceTags", "descriptorid"));
			
			if (savedDescId != null &&
					!savedDescId.equals(doc.getProperty("serviceTags", "descriptorid"))) {

				log.info("Service's descriptor modified, updating relations.");
				
				// Clear old relation
				RelationService.clearRelations(doc);
				if (savedDescId != null && !savedDescId.equals("")) {
					DocumentModel savedDesc = session.getDocument(new IdRef(savedDescId));
					if (savedDesc != null &&
								!savedDesc.getProperty("endpoints", "serviceid").equals("")) {
							RelationService.clearRelations(savedDesc);
							savedDesc.setProperty("endpoints", "serviceid", "");
							log.info("Old descriptor cleared.");
							// TODO: Save without saving loop...
					}
					else {
						log.warn("Old service document of ID "+savedDescId+" not found.");
					}
				}
				
				// Create new relation
				String newDescId = (String) doc.getProperty("serviceTags", "descriptorid");
				if (newDescId != null && !newDescId.equals("")) {
					DocumentModel newDesc = session.getDocument(new IdRef(newDescId));
					if (newDesc != null) {
						RelationService.createRelation(newDesc, doc);
						if (!newDesc.getProperty("endpoints", "serviceid").equals(doc.getId())) {
							newDesc.setProperty("endpoints", "serviceid", doc.getId());
							log.info("New descriptor modified.");
							// TODO: Save without saving loop...
						}
					}
					else {
						log.error("Service document of ID "+newDescId+" not found.");
					}
				}
				
				/*
				// Allow several descriptors
				List<String> descriptors = (List<String>) doc.getProperty("serviceTags",
						"descriptors");
				RelationService.clearRelations(doc);
				for (String descriptor : descriptors) {
					DocumentModelList models = session
							.query("select * from Document where dc:title = '"
									+ descriptor + "'");

					for (DocumentModel model : models) {
						RelationService.createRelation(model, doc);
						model.setProperty("endpoint", "servicename", doc.getName());
						session.saveDocument(model);
					}
				}*/
				
			}
		} catch (Exception e) {
			log.error("Error while updating relations", e);
		}

		// Save
		try {
			session.save();
		} catch (Exception e) {
			log.error("Error while saving service", e);
		}
	}
}