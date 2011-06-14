package org.easysoa.listeners;

import static org.easysoa.doctypes.ServiceAPI.DOCTYPE;
import static org.easysoa.doctypes.ServiceAPI.PROP_APPLICATION;
import static org.easysoa.doctypes.ServiceAPI.PROP_PROTOCOLS;
import static org.easysoa.doctypes.ServiceAPI.PROP_URL;
import static org.easysoa.doctypes.ServiceAPI.SCHEMA;

import java.io.File;
import java.net.InetAddress;
import java.net.URL;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.easysoa.doctypes.AppliImpl;
import org.easysoa.doctypes.Service;
import org.easysoa.services.VocabularyService;
import org.easysoa.services.DocumentService;
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
import org.ow2.easywsdl.wsdl.api.WSDLReader;

public class ServiceAPIListener implements EventListener {
	
	private static final Log log = LogFactory.getLog(ServiceAPIListener.class);

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
		if (!type.equals(DOCTYPE)) {
			return;
		}
		
		String environment = null, server = null;
		
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
			
			try {

				// Analyze WSDL
				WSDLReader reader = WSDLFactory.newInstance().newWSDLReader();
				Description desc = reader.read(tmpFile.toURI().toURL());

				// Initialization
				org.ow2.easywsdl.wsdl.api.Service firstService = 
					(org.ow2.easywsdl.wsdl.api.Service) desc.getServices().get(0);
				
				// Default URL
				String url = (String) doc.getProperty(SCHEMA, PROP_URL);
				if (url == null) {
					Endpoint firstEndpoint = firstService.getEndpoints().get(0);
					if (server == null) {
						server = InetAddress.getByName(
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

				doc.setProperty(SCHEMA, PROP_URL, url);
				doc.setProperty(SCHEMA, PROP_PROTOCOLS,
						((Binding) ((Endpoint) firstService.getEndpoints().get(0))
								.getBinding()).getTransportProtocol());
				
				// Update parent's properties
				DocumentModel parentModel = session.getDocument(doc.getParentRef());
				String existingServer = (String) parentModel.getProperty(AppliImpl.SCHEMA, AppliImpl.PROP_SERVER);
				if (existingServer == null || !server.equals(existingServer)) {
					parentModel.setProperty(AppliImpl.SCHEMA, AppliImpl.PROP_SERVER, server);
				}
				try {
					String provider = new URL(((Endpoint) firstService.getEndpoints().get(0)).getAddress()).getAuthority();
					String existingProvider = (String) parentModel.getProperty(AppliImpl.SCHEMA, AppliImpl.PROP_PROVIDER);
					if (existingProvider == null || !provider.equals(existingProvider)) {
						parentModel.setProperty(AppliImpl.SCHEMA, AppliImpl.PROP_PROVIDER, provider);
					}
				}
				catch(Exception e) {
					// Nothing (authority extraction failed)
				}
				environment = (String) doc.getProperty(AppliImpl.SCHEMA, AppliImpl.PROP_ENVIRONMENT);
				if (environment == null || environment.isEmpty()) {
					parentModel.setProperty(AppliImpl.SCHEMA, AppliImpl.PROP_ENVIRONMENT, AppliImpl.DEFAULT_ENVIRONMENT);
					environment = AppliImpl.DEFAULT_ENVIRONMENT;
				}
				session.saveDocument(parentModel);
				
				// Generate services
				if (!creationEvent) {
					for (org.ow2.easywsdl.wsdl.api.Service service : desc.getServices()) {
						String serviceName = service.getQName().getLocalPart();
						if (DocumentService.findService(session, url) == null) {
							try {
								String serviceUrl = service.getEndpoints().get(0).getAddress();
								DocumentModel serviceModel = DocumentService.createService(session, 
										doc.getPathAsString(), serviceUrl);
								if (serviceModel != null) {
									serviceModel.setProperty("dublincore", "title", serviceName);
									if (serviceUrl.contains("PureAirFlowers")) { // XXX: Hard-coded PureAirFlowers Light URL
										serviceModel.setProperty(Service.SCHEMA, Service.PROP_LIGHTURL,
												"http://localhost:8083/easysoa/light/paf.html");
									}
									session.saveDocument(serviceModel);
								}
								else {
									throw new NullPointerException("Cannot find Service API for child service creation.");
								}
							}
							catch (Exception e) {
								log.warn("Cannot set extracted service url : "+e.getMessage());
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
		// TODO: Update on document deletion
		try {
			String app = (String) doc.getProperty(SCHEMA, PROP_APPLICATION);
			if (app != null && !app.isEmpty() && !VocabularyService.entryExists(
					session, VocabularyService.VOCABULARY_APPLICATION, app)) {
				VocabularyService.addEntry(session, VocabularyService.VOCABULARY_APPLICATION,
						app, app);
			}
		}
		catch (ClientException e) {
			log.error("Error while updating "+VocabularyService.VOCABULARY_APPLICATION+" vocabulary", e);
		}
		
	}
	
}