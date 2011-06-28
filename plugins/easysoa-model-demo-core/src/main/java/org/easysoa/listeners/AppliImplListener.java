package org.easysoa.listeners;

import static org.easysoa.doctypes.AppliImpl.DOCTYPE;
import static org.easysoa.doctypes.AppliImpl.PROP_ENVIRONMENT;
import static org.easysoa.doctypes.AppliImpl.PROP_SERVER;
import static org.easysoa.doctypes.AppliImpl.PROP_SERVERENTRY;
import static org.easysoa.doctypes.AppliImpl.SCHEMA;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.easysoa.services.VocabularyService;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.event.Event;
import org.nuxeo.ecm.core.event.EventContext;
import org.nuxeo.ecm.core.event.EventListener;
import org.nuxeo.ecm.core.event.impl.DocumentEventContext;

public class AppliImplListener implements EventListener {

	private static final Log log = LogFactory.getLog(AppliImplListener.class);

	public static final String DEFAULT_ENVIRONMENT = "Production";

	public void handleEvent(Event event) {

		// Check event type
		EventContext ctx = event.getContext();
		if (!(ctx instanceof DocumentEventContext)) {
			return;
		}
		CoreSession session = ctx.getCoreSession();
		DocumentModel doc = ((DocumentEventContext) ctx).getSourceDocument();

		// Check document type
		if (doc == null) {
			return;
		}
		String type = doc.getType();
		if (!type.equals(DOCTYPE)) {
			return;
		}
		
		// Maintain internal properties
		try {
			doc.setProperty(SCHEMA, PROP_SERVERENTRY, 
					doc.getProperty(SCHEMA, PROP_ENVIRONMENT) + "/" + 
					doc.getProperty(SCHEMA, PROP_SERVER));
		} catch (ClientException e) {
			log.error("Failed to maintain internal property", e);
		}

		// Update vocabulary
		// TODO: Update on document deletion
		try {
			
			String environment = (String) doc.getProperty(SCHEMA, PROP_ENVIRONMENT);
			String server = (String) doc.getProperty(SCHEMA, PROP_SERVER);

			if (environment == null)
				environment = DEFAULT_ENVIRONMENT;
		
			if (!VocabularyService.entryExists(session,
					VocabularyService.VOCABULARY_ENVIRONMENT, environment)) {
				VocabularyService.addEntry(session,
						VocabularyService.VOCABULARY_ENVIRONMENT,
						environment, environment);
			}
			if (server != null && !server.isEmpty()
					&& !VocabularyService.entryExists(session,
							VocabularyService.VOCABULARY_SERVER, server)) {
				VocabularyService.addEntry(session,
						VocabularyService.VOCABULARY_SERVER, server,
						server, environment);
			}
		} catch (ClientException e) {
			log.error("Error while updating vocabularies", e);
		}

	}

}