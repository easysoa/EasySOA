/**
 * EasySOA Registry
 * Copyright 2011 Open Wide
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * 
 * Contact : easysoa-dev@googlegroups.com
 */

package org.easysoa.services.webparsing;

import static org.easysoa.doctypes.Service.PROP_FILEURL;
import static org.easysoa.doctypes.Service.PROP_WSDLNAMESPACE;
import static org.easysoa.doctypes.Service.PROP_WSDLSERVICENAME;
import static org.easysoa.doctypes.Service.SCHEMA;

import java.io.File;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nuxeo.ecm.core.api.Blob;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.ow2.easywsdl.wsdl.WSDLFactory;
import org.ow2.easywsdl.wsdl.api.Description;
import org.ow2.easywsdl.wsdl.api.WSDLReader;

/**
 * 
 * Parses WSDLs to retrieve service information.
 * 
 * @author mkalam-alami
 *
 */
public class WSDLParser implements WebFileParser {

    private static Log log = LogFactory.getLog(WSDLParser.class);
    
    @Override
    public void parse(CoreSession session, Blob data, DocumentModel model,
            Map<String, String> options) {
        
        if (model != null) {

            File tmpFile = null;
            
            try {
                // Extract file to system for analysis
                tmpFile = File.createTempFile(model.getId(), null);
                data.transferTo(tmpFile);
                   
                // Analyze WSDL
                WSDLReader reader = WSDLFactory.newInstance().newWSDLReader();
                Description desc = reader.read(tmpFile.toURI().toURL());

                // Initialization
                org.ow2.easywsdl.wsdl.api.Service firstService = 
                    (org.ow2.easywsdl.wsdl.api.Service) desc.getServices().get(0);
                
                // Namespace extraction
                String namespace = desc.getTargetNamespace();
                model.setProperty(SCHEMA, PROP_WSDLNAMESPACE, namespace);
                
                // URL extraction
                /*Endpoint firstEndpoint = firstService.getEndpoints().get(0);
                url = PropertyNormalizer.normalizeUrl(firstEndpoint.getAddress());
                doc.setProperty(SCHEMA, PROP_URL, url);*/
                
                // Service name extraction
                String serviceName = firstService.getQName().getLocalPart();
                model.setProperty(SCHEMA, PROP_WSDLSERVICENAME, serviceName);
                String title = (String) model.getProperty("dublincore", "title");
                String fileUrl = (String) model.getProperty(SCHEMA, PROP_FILEURL);
                if (title == null || title.isEmpty() || title.equals(fileUrl)) {
                    model.setProperty("dublincore", "title", serviceName);
                }
                
                //// Update parent's properties
                
                // Supported protocols
                // FIXME Triggers infinite loops
                
               /* if (doc.getParentRef() != null) {
                    
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
                }*/

            } catch (Exception e) {
                log.warn("WSDL parsing failed", e);
            } finally {
                if (tmpFile != null) {
                    tmpFile.delete();
                }
            }
            
        }
        
    }
    
}
