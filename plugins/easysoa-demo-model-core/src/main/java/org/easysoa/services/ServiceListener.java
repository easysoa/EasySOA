package org.easysoa.services;

import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.easysoa.tools.RelationService;
import org.easysoa.tools.VocabularyService;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentModelList;
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
	
	private static final Log log = LogFactory.getLog(ServiceListener.class);
	private static final String SERVICE_DOCTYPE = "Service";
	private static final String SERVICE_VOCABULARY = "servicelist";

	@SuppressWarnings("unchecked")
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
		
		// Service list vocabulary management
		try {
			String name = doc.getTitle();
			if (VocabularyService.entryExists(session, SERVICE_VOCABULARY, name))
				VocabularyService.removeEntry(session, SERVICE_VOCABULARY, name);
			VocabularyService.addEntry(session, SERVICE_VOCABULARY, name, name);
		} catch (Exception e) {
			log.error("Error while updating vocabulary", e);
		}

		// Manage relations with descriptors
		try {
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

/*
 * Location:
 * /data/home/mkalam-alami/workspace/decompilation/easysoa-demo-model-core
 * -0.1.1/ Qualified Name: org.easysoa.services.ServiceListener JD-Core Version:
 * 0.6.0
 */