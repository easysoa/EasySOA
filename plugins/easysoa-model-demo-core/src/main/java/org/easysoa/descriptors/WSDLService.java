package org.easysoa.descriptors;

import java.io.File;
import java.net.InetAddress;
import java.net.URL;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.easysoa.tools.VocabularyService;
import org.nuxeo.ecm.core.api.Blob;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.IdRef;
import org.ow2.easywsdl.wsdl.WSDLFactory;
import org.ow2.easywsdl.wsdl.api.Binding;
import org.ow2.easywsdl.wsdl.api.Description;
import org.ow2.easywsdl.wsdl.api.Endpoint;
import org.ow2.easywsdl.wsdl.api.Service;
import org.ow2.easywsdl.wsdl.api.WSDLReader;

/**
 * WSDL creation/modification handling, for metadata extraction, and relations
 * management.
 * 
 * @author mkalam-alami
 * 
 */
public class WSDLService {

	private static final Log log = LogFactory.getLog(WSDLService.class);
	public static final String WSDL_DOCTYPE = "WSDL";
	private static final String DEFAULT_ENVIRONMENT = "Deployment";

	public static void update(CoreSession session, DocumentModel doc) {
		try {
			
			Blob blob = (Blob) doc.getProperty("file", "content");
			File tmpFile = File.createTempFile(doc.getId(), null);
			blob.transferTo(tmpFile);
			
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

				doc.setProperty("endpoints", "name", ((Endpoint) service
						.getEndpoints().get(0)).getName());

				doc.setProperty("endpoints", "uri", uris);
				doc.setProperty("endpoints", "machine", machine);

				String formProvider = (String) doc.getProperty("endpoints", "provider");
				if (formProvider == null || formProvider.isEmpty()) {
					try {
						doc.setProperty("endpoints", "provider", new URL(
							((Endpoint) service.getEndpoints().get(0))
									.getAddress()).getAuthority());
					}
					catch(Exception e) {
						// Nothing (authority extraction failed)
					}
				}

				environment = (String) doc.getProperty("endpoints", "environment");
				if (environment == null || environment.isEmpty()) {
					doc.setProperty("endpoints", "environment", DEFAULT_ENVIRONMENT);
					environment = DEFAULT_ENVIRONMENT;
				}
				doc.setProperty("endpoints", "protocols",
						((Binding) ((Endpoint) service.getEndpoints().get(0))
								.getBinding()).getTransportProtocol());

				doc.setProperty("endpoints", "location",
						doc.getProperty("endpoints", "environment")
						+ "/" + doc.getProperty("endpoints", "machine"));

			} catch (Exception e) {
				log.error("WSDL parsing failed", e);
			} finally {
				tmpFile.delete();
			}

			DocumentModel newService = null;
			String newServiceId = (String) doc.getProperty("endpoint", "serviceid");
			if (newServiceId != null && !newServiceId.isEmpty()) {
				newService = session.getDocument(new IdRef(newServiceId));
			}

			// Metadata : update implementations summary for the related service
			if (newService != null) {
				newService.setProperty("serviceTags", "implementations",
						(String) doc.getProperty("endpoints", "implementation"));
				session.saveDocument(newService);
			}

			// Environment/Machine vocabulary management
			try {
				if (machine != null && environment != null &&
						!VocabularyService.entryExists(session, "machine", machine))
					if (!VocabularyService.entryExists(session, "environment", environment))
						VocabularyService.addEntry(session, "environment", environment, environment);
					VocabularyService.addEntry(session, "machine", machine, machine, environment);
			} catch (Exception e1) {
				log.error("Cannot verify vocabulary");
			}

			// Save
			session.save();

		} catch (Exception e) {
			log.error("Error while importing WSDL", e);
		}
	}
}