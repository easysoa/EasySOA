package org.easysoa.listeners;

import static org.easysoa.doctypes.Service.*;

import java.io.File;
import java.net.InetAddress;
import java.net.URL;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.easysoa.doctypes.AppliImpl;
import org.easysoa.doctypes.Service;
import org.easysoa.doctypes.ServiceAPI;
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
import org.ow2.easywsdl.wsdl.api.WSDLReader;

public class ServiceListener implements EventListener {
	
	private static final Log log = LogFactory.getLog(ServiceListener.class);

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
		
		String environment = null, server = null;
		
		try {
			// Extract data from specified document
			String fileUrl = (String) doc.getProperty(SCHEMA_COMMON, PROP_FILEURL);
			if (fileUrl != null) {

				// Parse the WSDL once, then forget it
				doc.setProperty(SCHEMA_COMMON, PROP_FILEURL, null);
				
				// Download file
				Blob blob = new HttpFile(fileUrl).download().getBlob();
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
					
					// URL extraction
					Endpoint firstEndpoint = firstService.getEndpoints().get(0);
					String	url = firstEndpoint.getAddress();
					doc.setProperty(SCHEMA, PROP_URL, url);
					
					// Server extraction
					if (server == null) {
						server = InetAddress.getByName(
								new URL(firstEndpoint.getAddress()).getHost())
								.getHostAddress();
					}
					
					// Service name extraction
					String title  = (String) doc.getProperty("dublincore", "title");
					if (title == null || title.isEmpty() || title.equals(url)) {
						doc.setProperty("dublincore", "title", firstService.getQName().getLocalPart());
					}
					
					// EasySOA Light 
					// XXX: Hard-coded PureAirFlowers Light URL
					if (url.contains("PureAirFlowers")) { 
						doc.setProperty(SCHEMA, PROP_LIGHTURL,
								"http://localhost:8083/easysoa/light/paf.html");
					}
					
					session.saveDocument(doc);
					
					//// Update parent's properties
					
					// Supported protocols
					DocumentModel apiModel = session.getDocument(doc.getParentRef());
					String protocol = ((Binding) ((Endpoint) firstService.getEndpoints().get(0))
							.getBinding()).getTransportProtocol();
					String storedProtocol = (String) apiModel.getProperty(
							ServiceAPI.SCHEMA, ServiceAPI.PROP_PROTOCOLS);
					if (storedProtocol == null || !storedProtocol.contains(protocol)) {
						if (storedProtocol == null || storedProtocol.isEmpty()) {
							apiModel.setProperty(ServiceAPI.SCHEMA, ServiceAPI.PROP_PROTOCOLS, protocol);
						}
						else {
							apiModel.setProperty(ServiceAPI.SCHEMA, ServiceAPI.PROP_PROTOCOLS,
									storedProtocol + ", " + protocol);
						}
					}

					DocumentModel appliImplModel = session.getDocument(apiModel.getParentRef());
					
					// Server
					String existingServer = (String) appliImplModel.getProperty(AppliImpl.SCHEMA, AppliImpl.PROP_SERVER);
					if (existingServer == null || !server.equals(existingServer)) {
						appliImplModel.setProperty(AppliImpl.SCHEMA, AppliImpl.PROP_SERVER, server);
					}
					
					// Provider
					try {
						String provider = new URL(((Endpoint) firstService.getEndpoints().get(0)).getAddress()).getAuthority();
						String existingProvider = (String) appliImplModel.getProperty(AppliImpl.SCHEMA, AppliImpl.PROP_PROVIDER);
						if (existingProvider == null || !provider.equals(existingProvider)) {
							appliImplModel.setProperty(AppliImpl.SCHEMA, AppliImpl.PROP_PROVIDER, provider);
						}
					}
					catch(Exception e) {
						// Nothing (authority extraction failed)
					}
					
					// Environment
					environment = (String) appliImplModel.getProperty(AppliImpl.SCHEMA, AppliImpl.PROP_ENVIRONMENT);
					if (environment == null || environment.isEmpty()) {
						appliImplModel.setProperty(AppliImpl.SCHEMA, AppliImpl.PROP_ENVIRONMENT, AppliImpl.DEFAULT_ENVIRONMENT);
						environment = AppliImpl.DEFAULT_ENVIRONMENT;
					}
					
					session.saveDocument(apiModel);
	
				} catch (Exception e) {
					log.error("WSDL parsing failed", e);
				} finally {
					tmpFile.delete();
				}
	
				// Save
				session.save();
	
			}
		} catch (Exception e) {
			log.error("Error while parsing WSDL", e);
		}
		
	}
	
}