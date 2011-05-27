package org.easysoa.listeners;

import java.io.File;
import java.net.InetAddress;
import java.net.URL;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.easysoa.services.DocumentService;
import org.nuxeo.ecm.core.api.Blob;
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
		if (!type.equals(DocumentService.SERVICEAPI_DOCTYPE)) {
			return;
		}
		
		// If the file contains a WSDL file, parse it
		try {
			
			Blob blob = (Blob) doc.getProperty("file", "content");
			File tmpFile = File.createTempFile(doc.getId(), null);
			blob.transferTo(tmpFile);
			
			// TODO: Test if WSDL
			
			String environment = null, machine = null;
			
			try {

				// Analyze WSDL
				WSDLReader reader = WSDLFactory.newInstance().newWSDLReader();
				Description desc = reader.read(tmpFile.toURI().toURL());

				// Initialization
				Service service = (Service) desc.getServices().get(0);
				String uris = "";
				for (Endpoint endpoint : service.getEndpoints()) {
					uris = uris + ", " + endpoint.getAddress();
					if (machine == null) {
						machine = InetAddress.getByName(
								new URL(endpoint.getAddress()).getHost())
								.getHostAddress();
					}

				}

				// Fill document metadata

				uris = uris.substring(2);

				doc.setProperty("dublincore", "title",
						service.getQName().getLocalPart() + " WSDL");

				String name = (String) doc.getProperty("apidef", "name");
				if (name == null || name.isEmpty()) {
					doc.setProperty("apidef", "name", ((Endpoint) service
							.getEndpoints().get(0)).getName());
				}

				doc.setProperty("apidef", "uri", uris);
				doc.setProperty("apidef", "machine", machine);

				String formProvider = (String) doc.getProperty("endpoints", "provider");
				if (formProvider == null || formProvider.isEmpty()) {
					try {
						doc.setProperty("apidef", "provider", new URL(
							((Endpoint) service.getEndpoints().get(0))
									.getAddress()).getAuthority());
					}
					catch(Exception e) {
						// Nothing (authority extraction failed)
					}
				}

				environment = (String) doc.getProperty("apidef", "environment");
				if (environment == null || environment.isEmpty()) {
					doc.setProperty("apidef", "environment", DEFAULT_ENVIRONMENT);
					environment = DEFAULT_ENVIRONMENT ;
				}
				doc.setProperty("apidef", "protocols",
						((Binding) ((Endpoint) service.getEndpoints().get(0))
								.getBinding()).getTransportProtocol());

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
	}
}