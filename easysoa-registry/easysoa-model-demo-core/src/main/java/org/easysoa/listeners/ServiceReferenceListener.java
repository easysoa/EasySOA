package org.easysoa.listeners;

import static org.easysoa.doctypes.ServiceReference.DOCTYPE;
import static org.easysoa.doctypes.ServiceReference.PROP_REFURL;
import static org.easysoa.doctypes.ServiceReference.SCHEMA;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.easysoa.doctypes.Service;
import org.easysoa.services.DocumentService;
import org.easysoa.services.NotificationService;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.event.Event;
import org.nuxeo.ecm.core.event.EventContext;
import org.nuxeo.ecm.core.event.EventListener;
import org.nuxeo.ecm.core.event.impl.DocumentEventContext;
import org.nuxeo.runtime.api.Framework;

public class ServiceReferenceListener implements EventListener {
	
	private static final Log log = LogFactory.getLog(ServiceReferenceListener.class);

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
		
		try {

			DocumentService docService = Framework.getService(DocumentService.class);
			NotificationService notifService = Framework.getService(NotificationService.class);
			
			// Create service from WSDL if it doesn't exist
			String refUrl = (String) doc.getProperty(SCHEMA, PROP_REFURL);
			if (refUrl != null && !refUrl.isEmpty()) {
				DocumentModel referenceService = docService.findService(session, refUrl);
				if (referenceService == null) {
					Map<String, String> properties = new HashMap<String, String>();
					properties.put(Service.PROP_URL, refUrl);
					notifService.notifyService(session, properties);
				}
			}
			
			session.save();
			
		} catch (Exception e) {
			log.error("Error while parsing WSDL", e);
		}
		
	}
	
}