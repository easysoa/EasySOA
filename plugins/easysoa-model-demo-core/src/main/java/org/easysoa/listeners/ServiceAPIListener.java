package org.easysoa.listeners;

import java.io.File;
import java.net.InetAddress;
import java.net.URL;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.easysoa.services.DocumentService;
import org.easysoa.services.VocabularyService;
import org.nuxeo.ecm.core.api.Blob;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.event.Event;
import org.nuxeo.ecm.core.event.EventContext;
import org.nuxeo.ecm.core.event.EventListener;
import org.nuxeo.ecm.core.event.impl.DocumentEventContext;
import org.ow2.easywsdl.wsdl.WSDLFactory;
import org.ow2.easywsdl.wsdl.api.Binding;
import org.ow2.easywsdl.wsdl.api.Description;
import org.ow2.easywsdl.wsdl.api.Endpoint;
import org.ow2.easywsdl.wsdl.api.Service;
import org.ow2.easywsdl.wsdl.api.WSDLReader;

public class ServiceAPIListener implements EventListener {
	
	private static final Log log = LogFactory.getLog(ServiceAPIListener.class);

	public static final String SERVICEAPIDEF_SCHEMA = "serviceapidef";
	public static final String DEFAULT_ENVIRONMENT = "Production";
	public static final String SERVICEDEF_SCHEMA = "servicedef";

	public void handleEvent(Event event) {
		
		// Check event type
		EventContext ctx = event.getContext();
		if (!(ctx instanceof DocumentEventContext)) {
			return;
		}
		boolean creationEvent = event.getName().equals("documentCreated");
		
		CoreSession session = ctx.getCoreSession();
		DocumentModel doc = ((DocumentEventContext) ctx).getSourceDocument();
		
		// Check document type
		if (doc == null) {
			return;
		}
		String type = doc.getType();
		if (!type.equals(DocumentService.SERVICEAPI_DOCTYPE)) {
			return;
		}
		
		// If the file contains a WSDL file, parse it
		try {
			
			Blob blob = (Blob) doc.getProperty("file", "content");
			
			// Check that the API contains a file
			if (blob == null) {
				return;
			}
			
			// Extract file to system 
			File tmpFile = File.createTempFile(doc.getId(), null);
			blob.transferTo(tmpFile);
			
			// TODO: Test if WSDL
			
			String environment = null, machine = null;
			
			try {

				// Analyze WSDL
				WSDLReader reader = WSDLFactory.newInstance().newWSDLReader();
				Description desc = reader.read(tmpFile.toURI().toURL());

				// Initialization
				Service firstService = (Service) desc.getServices().get(0);
				
				// Default URL
				String url = (String) doc.getProperty(SERVICEAPIDEF_SCHEMA, "url");
				if (url == null) {
					Endpoint firstEndpoint = firstService.getEndpoints().get(0);
					if (machine == null) {
						machine = InetAddress.getByName(
								new URL(firstEndpoint.getAddress()).getHost())
								.getHostAddress();
					}
					url = firstEndpoint.getAddress();
				}
				
				// Fill document metadata

				String title  = (String) doc.getProperty("dublincore", "title");
				if (title == null || title.isEmpty()) {
					doc.setProperty("dublincore", "title", firstService.getQName().getLocalPart());
				}

				doc.setProperty(SERVICEAPIDEF_SCHEMA, "url", url);
				doc.setProperty(SERVICEAPIDEF_SCHEMA, "machine", machine);

				String formProvider = (String) doc.getProperty(SERVICEAPIDEF_SCHEMA, "provider");
				if (formProvider == null || formProvider.isEmpty()) {
					try {
						doc.setProperty(SERVICEAPIDEF_SCHEMA, "provider", new URL(
							((Endpoint) firstService.getEndpoints().get(0))
									.getAddress()).getAuthority());
					}
					catch(Exception e) {
						// Nothing (authority extraction failed)
					}
				}

				environment = (String) doc.getProperty(SERVICEAPIDEF_SCHEMA, "environment");
				if (environment == null || environment.isEmpty()) {
					doc.setProperty(SERVICEAPIDEF_SCHEMA, "environment", DEFAULT_ENVIRONMENT);
					environment = DEFAULT_ENVIRONMENT;
				}
				doc.setProperty(SERVICEAPIDEF_SCHEMA, "protocols",
						((Binding) ((Endpoint) firstService.getEndpoints().get(0))
								.getBinding()).getTransportProtocol());
				
				// Generate services
				if (!creationEvent) {
					for (Service service : desc.getServices()) {
						String serviceName = service.getQName().getLocalPart();
						if (DocumentService.findService(session, url) == null) {
							DocumentModel serviceModel = DocumentService.createService(session, url, serviceName);
							if (serviceModel != null) {
								try {
									String serviceUrl = service.getEndpoints().get(0).getAddress();
									serviceModel.setProperty(SERVICEDEF_SCHEMA, "url", serviceUrl);
									if (url.contains("PureAirFlowers")) { // XXX: Hard-coded PureAirFlowers Light URL
										serviceModel.setProperty(SERVICEDEF_SCHEMA, "lightUrl", "http://localhost:8083/easysoa/light/paf.html");
									}
								}
								catch (Exception e) {
									log.warn("Cannot set extracted service url : "+e.getMessage());
								}
							}
							else {
								throw new NullPointerException("Cannot find Service API for child service creation.");
							}
						}
					}
				}
				else {
					// Service creation fails on API creation (TODO: why?)
					session.saveDocument(doc);
				}

			} catch (Exception e) {
				log.error("WSDL parsing failed", e);
			} finally {
				tmpFile.delete();
			}

			// Save
			session.save();

		} catch (Exception e) {
			log.error("Error while parsing WSDL", e);
		}
		
		// Update vocabulary
		try {
			String app = (String) doc.getProperty(SERVICEAPIDEF_SCHEMA, "application");
			if (app != null && !app.isEmpty() && !VocabularyService.entryExists(
					session, VocabularyService.VOCBULARY_APPLICATION, app)) {
				VocabularyService.addEntry(session, VocabularyService.VOCBULARY_APPLICATION,
						app, app);
			}
			String environment = (String) doc.getProperty(SERVICEAPIDEF_SCHEMA, "environment");
			
			if (environment != null && !environment.isEmpty()) {
				if (!VocabularyService.entryExists(
					session, VocabularyService.VOCBULARY_ENVIRONMENT, environment)) {
					VocabularyService.addEntry(session, VocabularyService.VOCBULARY_ENVIRONMENT,
							environment, environment);
				}
				String machine = (String) doc.getProperty(SERVICEAPIDEF_SCHEMA, "machine");
				if (machine != null && !machine.isEmpty() && !VocabularyService.entryExists(
						session, VocabularyService.VOCBULARY_MACHINE, machine)) {
					VocabularyService.addEntry(session, VocabularyService.VOCBULARY_MACHINE,
							machine, machine, environment);
				}
			}
		}
		catch (ClientException e) {
			log.error("Error while updating "+VocabularyService.VOCBULARY_APPLICATION+" vocabulary", e);
		}
		
	}
	
}