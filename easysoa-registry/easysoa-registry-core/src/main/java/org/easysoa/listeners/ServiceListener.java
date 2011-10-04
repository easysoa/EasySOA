package org.easysoa.listeners;

import static org.easysoa.doctypes.Service.DOCTYPE;
import static org.easysoa.doctypes.Service.PROP_FILEURL;
import static org.easysoa.doctypes.Service.PROP_URL;
import static org.easysoa.doctypes.Service.SCHEMA;

import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URL;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.easysoa.EasySOAConstants;
import org.easysoa.doctypes.AppliImpl;
import org.easysoa.doctypes.PropertyNormalizer;
import org.easysoa.doctypes.ServiceAPI;
import org.easysoa.services.DocumentService;
import org.easysoa.services.NotificationService;
import org.nuxeo.ecm.core.api.Blob;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentModelList;
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
public class ServiceListener implements EventListener {
    
    private static Log log = LogFactory.getLog(ServiceListener.class);

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
            
            // Extract data from document
            String title = (String) doc.getProperty("dublincore", "title");
            String url = (String) doc.getProperty(SCHEMA, PROP_URL);
            String fileUrl = (String) doc.getProperty(SCHEMA, PROP_FILEURL);

            // Extract data from WSDL
            if (fileUrl != null) {
                
                try {
                    
                    // Download file
                    Blob blob = null;
                    try {
                        blob = new HttpFile(new URL(fileUrl)).download().getBlob();
                    }
                    catch (IOException e) {
                        log.info("I/O Error while downloading attached WSDL '" + fileUrl + "': " + e.getMessage());
                    }
                    catch (Exception e) {
                        log.info("Failed to download attached WSDL '" + fileUrl + "': " + e.getMessage());
                    }
                    
                    if (blob != null) {
                        
                        // Save WSDLblob
                        NotificationService notifService = Framework.getService(NotificationService.class);
                        doc.setProperty("file", "content", blob);
                        doc.setProperty("file", "filename", notifService.computeServiceTitle(fileUrl)+".wsdl");
                        
                        // Extract file to system for analysis
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
                            url = PropertyNormalizer.normalizeUrl(firstEndpoint.getAddress());
                            doc.setProperty(SCHEMA, PROP_URL, url);
                            
                            // Service name extraction
                            if (title == null || title.isEmpty() || title.equals(fileUrl)) {
                                title = firstService.getQName().getLocalPart();
                                doc.setProperty("dublincore", "title", title);
                            }
                            
                            //// Update parent's properties
                            
                            // Supported protocols
                            
                            if (doc.getParentRef() != null) {
                                
                                DocumentModel apiModel = session.getDocument(doc.getParentRef());
                                String storedProtocol = (String) apiModel.getProperty(
                                        ServiceAPI.SCHEMA, ServiceAPI.PROP_PROTOCOLS);
                                try {
                                    String protocol = ((Binding) ((Endpoint) firstService.getEndpoints().get(0))
                                            .getBinding()).getTransportProtocol();
                                    if (storedProtocol == null || !storedProtocol.contains(protocol)) {
                                        if (storedProtocol == null || storedProtocol.isEmpty()) {
                                            apiModel.setProperty(ServiceAPI.SCHEMA, ServiceAPI.PROP_PROTOCOLS, protocol);
                                        }
                                        else {
                                            apiModel.setProperty(ServiceAPI.SCHEMA, ServiceAPI.PROP_PROTOCOLS,
                                                    storedProtocol + ", " + protocol);
                                        }
                                    }
                                }
                                catch (Exception e) {
                                    log.warn("Failed to extract protocol from WSDL: "+e.getMessage());
                                }
        
                                if (apiModel.getParentRef() != null) {
                                        
                                    DocumentModel appliImplModel = session.getDocument(apiModel.getParentRef());
                                    
                                    // Server
                                    String existingServer = (String) appliImplModel.
                                            getProperty(AppliImpl.SCHEMA, AppliImpl.PROP_SERVER);
                                    String newServer = InetAddress.getByName(
                                                new URL(firstEndpoint.getAddress()).getHost())
                                                .getHostAddress();
                                    if (existingServer == null || !newServer.equals(existingServer)) {
                                        appliImplModel.setProperty(AppliImpl.SCHEMA, AppliImpl.PROP_SERVER, newServer);
                                    }
                                    
                                    // Provider
                                    try {
                                        String provider = new URL(((Endpoint) firstService.getEndpoints().get(0))
                                                .getAddress()).getAuthority();
                                        String existingProvider = (String) appliImplModel
                                                .getProperty(AppliImpl.SCHEMA, AppliImpl.PROP_PROVIDER);
                                        if (existingProvider == null || !provider.equals(existingProvider)) {
                                            appliImplModel.setProperty(AppliImpl.SCHEMA,
                                                    AppliImpl.PROP_PROVIDER, provider);
                                        }
                                    }
                                    catch(Exception e) {
                                        // Nothing (authority extraction failed)
                                    }
                                    
                                    session.saveDocument(appliImplModel);
                                }
                                
                                session.saveDocument(apiModel);
                            }
            
                        } catch (Exception e) {
                            log.error("WSDL parsing failed", e);
                        } finally {
                            tmpFile.delete();
                        }
                    }
            
                } catch (Exception e) {
                    log.error("Error while parsing WSDL", e);
                }
            }
            
            // Maintain properties
            if (url != null) {
                try {
                    doc.setProperty(SCHEMA, PROP_URL, PropertyNormalizer.normalizeUrl(url));
                } catch (MalformedURLException e) {
                    log.warn("Failed to normalize URL", e);
                }
            }
            if (fileUrl != null && !fileUrl.isEmpty()) {
                // XXX: Hacked Airport Light URL
                if (fileUrl.contains("irport")) {
                    doc.setProperty(SCHEMA, PROP_FILEURL, 
                            "http://localhost:"+EasySOAConstants.WEB_PORT+"/easysoa/light/modified_airport_soap.wsdl");
                }
                else {
                    doc.setProperty(SCHEMA, PROP_FILEURL, PropertyNormalizer.normalizeUrl(fileUrl));
                }
            }
            else {
                String potentialWsdlUrl = url+"?wsdl";
                HttpFile file = new HttpFile(new URL(potentialWsdlUrl));
                if (file.isFileAvailable()) {
                    doc.setProperty(SCHEMA, PROP_FILEURL, PropertyNormalizer.normalizeUrl(potentialWsdlUrl));
                }
            }
            session.save();
            
            // Test if the service already exists, delete the other one(s) if necessary
            try {
                DocumentService docService = Framework.getService(DocumentService.class);
                DocumentModelList existingServiceModels = session.query(
                        "SELECT * FROM Service WHERE serv:url = '" + url + "'");
                for (DocumentModel existingServiceModel : existingServiceModels) {
                    if (existingServiceModel != null && !existingServiceModel.getRef().equals(doc.getRef())) {
                        docService.mergeDocument(session, existingServiceModel, doc, false);
                    }
                }
            } catch (Exception e) {
                log.error("Error while trying to merge documents", e);
            }

        } catch (Exception e) {
            log.error(e);
        }

    }
    
}