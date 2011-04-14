package org.easysoa.descriptors;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.easysoa.tools.VocabularyService;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentModelList;
import org.nuxeo.ecm.core.event.Event;
import org.nuxeo.ecm.core.event.EventContext;
import org.nuxeo.ecm.core.event.EventListener;
import org.nuxeo.ecm.core.event.impl.DocumentEventContext;

/**
 * Entry point for service descriptors handling (creation/modification).
 * Manages descriptor list vocabulary, and allows doctype-specific classes to do further processing.
 * @author mkalam-alami
 *
 */
public class DescriptorListener implements EventListener {
	
	private static final Log log = LogFactory.getLog(DescriptorListener.class);
	private static final String DESCRIPTOR_DOCTYPE = "ServiceDescriptor";
	private static final String DESCRIPTOR_VOCABULARY = "descriptorlist";

	// TODO : Reset user cache so that the relations are refreshed in the browser
	/*@In(create = true)
	protected transient NavigationContext navigationContext;*/

	public void handleEvent(Event event) {
		
		// Check event type
		EventContext ctx = event.getContext();
		if (!(ctx instanceof DocumentEventContext)) {
			return;
		}
		
		CoreSession session = ctx.getCoreSession();

		// Check document type + Document-type specific processing
		DocumentModel doc = ((DocumentEventContext) ctx).getSourceDocument();
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

		// Descriptor list vocaulary management
		try {
			
			// New document = new entry
			if (event.getName().equals("documentCreated")) {
				String name = doc.getTitle();
				if (!VocabularyService.entryExists(session, DESCRIPTOR_VOCABULARY,
						name)) {
					VocabularyService.addEntry(session, DESCRIPTOR_VOCABULARY, name,
							name);
				}

			// Rebuild the whole descriptor list vocaulary
			// (TODO : instead, remove only the current document entry, fetching the old document by ID)
			} else {
				VocabularyService.removeAllEntries(session, DESCRIPTOR_VOCABULARY);
				DocumentModelList models = session
						.query("SELECT * FROM Document WHERE ecm:primaryType = 'ServiceDescriptor'OR ecm:primaryType = 'WSDL'");

				List<String> names = new ArrayList<String>();
				for (DocumentModel model : models) {
					names.add(model.getTitle());
				}
				VocabularyService.addEntries(session, DESCRIPTOR_VOCABULARY, names);
			}
		} catch (Exception e) {
			log.error("Error while updating vocabulary", e);
		}

		// Save
		try {
			session.save();
			/* NavigationContext navigationCxt = (NavigationContext) Contexts
					.getConversationContext().get("navigationContext");
			navigationCxt.resetCurrentContext();*/
		} catch (Exception e) {
			log.error("Error while saving descriptor", e);
		}
	}
}