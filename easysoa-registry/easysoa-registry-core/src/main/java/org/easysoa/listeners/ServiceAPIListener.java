package org.easysoa.listeners;

import static org.easysoa.doctypes.AppliImpl.PROP_URL;
import static org.easysoa.doctypes.AppliImpl.SCHEMA;
import static org.easysoa.doctypes.ServiceAPI.DOCTYPE;
import static org.easysoa.doctypes.ServiceAPI.PROP_APPLICATION;
import static org.easysoa.doctypes.ServiceAPI.PROP_PROTOCOLS;

import java.io.File;
import java.net.URL;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.easysoa.doctypes.AppliImpl;
import org.easysoa.doctypes.Service;
import org.easysoa.properties.PropertyNormalizer;
import org.easysoa.services.DocumentService;
import org.easysoa.services.VocabularyHelper;
import org.nuxeo.ecm.core.api.Blob;
import org.nuxeo.ecm.core.api.ClientException;
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

/**
 * 
 * @author mkalam-alami
 *
 */
public class ServiceAPIListener implements EventListener {

    private static Log log = LogFactory.getLog(ServiceAPIListener.class);

    public void handleEvent(Event event) {
        // TODO Rework exception handling
        
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

        // If the file contains a WSDL file, parse it
        try {

            String url = (String) doc.getProperty(SCHEMA, PROP_URL);

            Blob blob = (Blob) doc.getProperty("file", "content");

            // Check that the API contains a file
            if (blob != null) {

                // Extract file to system
                File tmpFile = File.createTempFile(doc.getId(), null);
                blob.transferTo(tmpFile);

                // TODO: Test if WSDL

                try {

                    // Analyze WSDL
                    WSDLReader reader = WSDLFactory.newInstance()
                            .newWSDLReader();
                    Description desc = reader.read(tmpFile.toURI().toURL());

                    // Initialization
                    org.ow2.easywsdl.wsdl.api.Service firstService = (org.ow2.easywsdl.wsdl.api.Service) desc
                            .getServices().get(0);

                    // Default URL
                    if (url == null) {
                        Endpoint firstEndpoint = firstService.getEndpoints()
                                .get(0);
                        url = firstEndpoint.getAddress();
                    }

                    // Fill document metadata

                    String title = (String) doc.getProperty("dublincore",
                            "title");
                    if (title == null || title.isEmpty()) {
                        doc.setProperty("dublincore", "title", firstService
                                .getQName().getLocalPart());
                    }

                    doc.setProperty(SCHEMA, PROP_URL, url);
                    doc.setProperty(SCHEMA, PROP_PROTOCOLS,
                            ((Binding) ((Endpoint) firstService.getEndpoints()
                                    .get(0)).getBinding())
                                    .getTransportProtocol());

                    // Update parent's properties
                    DocumentModel parentModel = session.getDocument(doc
                            .getParentRef());
                    
                    try {
                        String provider = new URL(((Endpoint) firstService
                                .getEndpoints().get(0)).getAddress())
                                .getAuthority();
                        String existingProvider = (String) parentModel
                                .getProperty(AppliImpl.SCHEMA,
                                        AppliImpl.PROP_PROVIDER);
                        if (existingProvider == null
                                || !provider.equals(existingProvider)) {
                            parentModel.setProperty(AppliImpl.SCHEMA,
                                    AppliImpl.PROP_PROVIDER, provider);
                        }
                    } catch (NullPointerException e) {
                        // Nothing (authority extraction failed)
                    }
                    
                    session.saveDocument(parentModel);

                    DocumentService docService = Framework.getRuntime()
                            .getService(DocumentService.class);

                    // Generate services
                    if (!creationEvent) {
                        for (org.ow2.easywsdl.wsdl.api.Service service : desc
                                .getServices()) {
                            String serviceName = service.getQName()
                                    .getLocalPart();
                            if (docService.findService(session, url) == null) {
                                try {
                                    String serviceUrl = service.getEndpoints()
                                            .get(0).getAddress();
                                    DocumentModel serviceModel = docService.createService(session,
                                                    doc.getPathAsString(), serviceUrl);
                                    if (serviceModel != null) {
                                        serviceModel.setProperty("dublincore", "title", serviceName);
                                        if (serviceUrl.contains("PureAirFlowers")) { // XXX: Hard-coded PureAirFlowers Light URL
                                            serviceModel.setProperty(Service.SCHEMA, Service.PROP_LIGHTURL,
                                                   "http://localhost:8083/easysoa/light/paf.html");
                                        }
                                        session.saveDocument(serviceModel);
                                    } else {
                                        throw new Exception("Cannot find Service API for child service creation.");
                                    }
                                } catch (Exception e) {
                                    log.warn("Cannot set extracted service url: "
                                            + e.getMessage());
                                }
                            }
                        }
                    } else {
                        // Service creation fails on API creation TODO: why?
                        session.saveDocument(doc);
                    }

                } catch (Exception e) {
                    log.error("WSDL parsing failed", e);
                } finally {
                    tmpFile.delete();
                }

            }

            // Maintain properties
            try {
                if (url != null) {
                    doc.setProperty(SCHEMA, PROP_URL,
                            PropertyNormalizer.normalizeUrl(url));
                }
            } catch (Exception e) {
                log.error("Failed to normalize URL", e);
            }

            // Save
            session.save();

        } catch (Exception e) {
            log.error("Error while parsing WSDL", e);
        }

        // Update vocabulary
        // TODO: Update on document deletion
        try {
            VocabularyHelper vocService = Framework.getRuntime().getService(
                    VocabularyHelper.class);

            String app = (String) doc.getProperty(SCHEMA, PROP_APPLICATION);
            if (app != null
                    && !app.isEmpty()
                    && !vocService.entryExists(session,
                            VocabularyHelper.VOCABULARY_APPLICATION, app)) {
                vocService.addEntry(session,
                        VocabularyHelper.VOCABULARY_APPLICATION, app, app);
            }
        } catch (ClientException e) {
            log.error("Error while updating "
                    + VocabularyHelper.VOCABULARY_APPLICATION + " vocabulary",
                    e);
        }

    }

}