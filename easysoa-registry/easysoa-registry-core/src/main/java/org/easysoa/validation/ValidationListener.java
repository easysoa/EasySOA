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

package org.easysoa.validation;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.easysoa.doctypes.Service;
import org.nuxeo.ecm.core.api.Blob;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.event.Event;
import org.nuxeo.ecm.core.event.EventContext;
import org.nuxeo.ecm.core.event.EventListener;
import org.nuxeo.ecm.core.event.impl.DocumentEventContext;
import org.nuxeo.ecm.platform.publisher.api.PublisherService;
import org.nuxeo.runtime.api.Framework;

/**
 * 
 * @author mkalam-alami
 * 
 */
public class ValidationListener implements EventListener {

    private static Log log = LogFactory.getLog(ValidationListener.class);

    /*private ValidationService validationService;*/

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
        
        // if env (workspace ?) and creation validate all for the first time
        
        // if app / api validate all app / api
        
        // if service validate it
        if (type.equals(Service.DOCTYPE) && !doc.isProxy()) {
            // XXX Just a publication POC
            try {
                // Init validation
                /*if (validationService == null) {
                    validationService = Framework.getService(ValidationService.class);
                }
                validationService.validate(doc);*/

                String serviceName = (String) doc.getTitle();
                
                ArrayList<String> errors = new ArrayList<String>();
                
                // get service in reference env
                PublisherService publisherService = Framework.getService(PublisherService.class);
                /*PublicationTree publicationTree = publisherService.get(doc);
                DocumentModel referenceEnvService = publicationTree.getExistingPublishedDocument(docLocation);*/
                DocumentModel referenceEnvService = null;
                
                // delta-based validation would be : ex. if wsdl changed, check it
                
                // for now only full validation :
                
                // check there is a reference service
                if (referenceEnvService == null) {
                    Boolean hasNotificationBeenApproved = (Boolean) referenceEnvService.getProperty("easysoa", "hasNotificationBeenApproved");
                    if (!hasNotificationBeenApproved) {
                        errors.add(serviceName + " : unknown service");
                    }
                }
                
                // check wsdl
                Blob referenceEnvWsdlBlob = (Blob) referenceEnvService.getProperty("file", "content");
                if (referenceEnvWsdlBlob != null) {
                    Blob wsdlBlob = (Blob) doc.getProperty("file", "content");
                    File referenceEnvWsdlFile = File.createTempFile("easysoa", "wsdl");
                    File wsdlFile = File.createTempFile("easysoa", "wsdl");
                    referenceEnvWsdlBlob.transferTo(referenceEnvWsdlFile);
                    wsdlBlob.transferTo(wsdlFile);
                    if (wsdlBlob != null && isFinerWsdlThan(wsdlFile, referenceEnvWsdlFile)) {
                        errors.add(serviceName + " : service has different wsdl");
                    }
                }
                
                // if runtime env only :
                
                // check discovered by browsing
                String discoveryTypeBrowsing = (String) referenceEnvService.getProperty("soacommon", "discoveryTypeBrowsing");
                if (discoveryTypeBrowsing == null || discoveryTypeBrowsing.trim().length() == 0) {
                    errors.add(serviceName + " : service not found by browsing");
                }
                
                // check discovered by monitoring
                String discoveryTypeMonitoring = (String) referenceEnvService.getProperty("soacommon", "discoveryTypeMonitoring");
                if (discoveryTypeMonitoring == null || discoveryTypeMonitoring.trim().length() == 0) {
                    errors.add(serviceName + " : service not found by browsing");
                }
                
                // execute related tests (?)
                
                // update errors display
                
            } catch (Exception e) {
                log.error("Failed to validate service", e);
            }

        }

    }

    private boolean isFinerWsdlThan(File wsdlFile, File referenceEnvWsdlFile) {
        try {
            FileInputStream wsdlFin = new FileInputStream(wsdlFile);
            FileInputStream referenceEnvWsdlFin = new FileInputStream(referenceEnvWsdlFile);
            byte[] referenceEnvBuf = new byte[1024];
            byte[] buf = new byte[1024];
            int referenceEnvNbRead, nbRead;
            while ((referenceEnvNbRead = referenceEnvWsdlFin.read(referenceEnvBuf)) > 0) {
                nbRead = wsdlFin.read(buf);
                if (nbRead != referenceEnvNbRead) {
                    
                }
            }
        } catch (IOException ioex) {
            return false;
        }
        return true;
    }

}