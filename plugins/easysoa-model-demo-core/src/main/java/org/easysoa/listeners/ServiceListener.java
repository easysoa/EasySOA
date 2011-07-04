package org.easysoa.listeners;

import static org.easysoa.doctypes.EasySOADoctype.PROP_FILEURL;
import static org.easysoa.doctypes.EasySOADoctype.SCHEMA_COMMON;
import static org.easysoa.doctypes.Service.DOCTYPE;
import static org.easysoa.doctypes.Service.PROP_LIGHTURL;
import static org.easysoa.doctypes.Service.PROP_URL;
import static org.easysoa.doctypes.Service.SCHEMA;

import java.io.File;
import java.net.InetAddress;
import java.net.URL;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.easysoa.doctypes.AppliImpl;
import org.easysoa.doctypes.PropertyNormalizer;
import org.easysoa.doctypes.ServiceAPI;
import org.easysoa.services.DocumentService;
import org.nuxeo.ecm.core.api.Blob;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.event.Event;
import org.nuxeo.ecm.core.event.EventContext;
import org.nuxeo.ecm.core.event.EventListener;
import org.nuxeo.ecm.core.event.impl.DocumentEventContext;
import org.nuxeo.runtime.api.Framework;
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
		
		try {
			// Extract data from specified document
			String title = (String) doc.getProperty("dublincore", "title");
			String url = (String) doc.getProperty(SCHEMA, PROP_URL);
			String fileUrl = (String) doc.getProperty(SCHEMA_COMMON, PROP_FILEURL);
			
			// Extract data from WSDL
			if (fileUrl != null) {
				
				// Download file
				Blob blob = new HttpFile(fileUrl).download().getBlob();
				if (blob == null) {
					return;
				}
				
				// Extract file to system 
				File tmpFile = File.createTempFile(doc.getId(), null);
				blob.transferTo(tmpFile);
				
				// Analyze WSDL
				try {
	
					WSDLReader reader = WSDLFactory.newInstance().newWSDLReader();
					Description desc = reader.read(tmpFile.toURI().toURL());
	
					// Initialization
					org.ow2.easywsdl.wsdl.api.Service firstService = 
						(org.ow2.easywsdl.wsdl.api.Service) desc.getServices().get(0);
					
					// URL extraction
					Endpoint firstEndpoint = firstService.getEndpoints().get(0);
					url = firstEndpoint.getAddress();
					doc.setProperty(SCHEMA, PROP_URL, PropertyNormalizer.normalizeUrl(url));
					
					// Test if the service already exists, delete the other one if necessary
					DocumentService docService = Framework.getService(DocumentService.class);
					DocumentModel existingServiceModel = docService.findService(session, url);
					if (existingServiceModel != null && !existingServiceModel.getRef().equals(doc.getRef())) {
						docService.mergeDocument(session, existingServiceModel, doc, false);
					}
					
					// Service name extraction
					if (title == null || title.isEmpty() || title.equals(url)) {
						doc.setProperty("dublincore", "title", firstService.getQName().getLocalPart());
					}
					
					// EasySOA Light 
					// XXX: Hard-coded PureAirFlowers Light URL
					if (url.contains("PureAirFlowers Orders")) { 
						doc.setProperty(SCHEMA, PROP_LIGHTURL,
								"http://localhost:8083/easysoa/light/paf.html");
					}
					
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
					String newServer = InetAddress.getByName(
								new URL(firstEndpoint.getAddress()).getHost())
								.getHostAddress();
					if (existingServer == null || !newServer.equals(existingServer)) {
						appliImplModel.setProperty(AppliImpl.SCHEMA, AppliImpl.PROP_SERVER, newServer);
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
					
					session.saveDocument(apiModel);
					session.saveDocument(appliImplModel);
	
				} catch (Exception e) {
					log.error("WSDL parsing failed", e);
				} finally {
					tmpFile.delete();
				}
	
			}
			
			// Default properties
			if (title == null || title.isEmpty() || title.equals(url)) {
				doc.setProperty("dublincore", "title", url.substring(title.lastIndexOf('/')+1));
			}
			
			session.save();
			
		} catch (Exception e) {
			log.error("Error while parsing WSDL", e);
		}

	}
	
}