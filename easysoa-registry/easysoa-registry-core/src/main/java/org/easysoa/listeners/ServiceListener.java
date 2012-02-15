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

package org.easysoa.listeners;

import static org.easysoa.doctypes.EasySOADoctype.PROP_DTBROWSING;
import static org.easysoa.doctypes.EasySOADoctype.PROP_DTDESIGN;
import static org.easysoa.doctypes.EasySOADoctype.PROP_DTIMPORT;
import static org.easysoa.doctypes.EasySOADoctype.PROP_DTMONITORING;
import static org.easysoa.doctypes.EasySOADoctype.SCHEMA_COMMON_PREFIX;
import static org.easysoa.doctypes.Service.DOCTYPE;
import static org.easysoa.doctypes.Service.PROP_FILEURL;
import static org.easysoa.doctypes.Service.PROP_REFERENCESERVICE;
import static org.easysoa.doctypes.Service.PROP_REFERENCESERVICEORIGIN;
import static org.easysoa.doctypes.Service.PROP_URL;
import static org.easysoa.doctypes.Service.PROP_URLTEMPLATE;
import static org.easysoa.doctypes.Service.SCHEMA;
import static org.easysoa.doctypes.Service.SCHEMA_PREFIX;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.SortedSet;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.easysoa.EasySOAConstants;
import org.easysoa.doctypes.Service;
import org.easysoa.properties.PropertyNormalizer;
import org.easysoa.services.DocumentService;
import org.easysoa.services.EventsHelper;
import org.easysoa.services.ServiceValidationService;
import org.easysoa.services.webparsing.WebFileParsingPoolService;
import org.easysoa.validation.CorrelationMatch;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentModelList;
import org.nuxeo.ecm.core.api.PathRef;
import org.nuxeo.ecm.core.event.Event;
import org.nuxeo.ecm.core.event.EventContext;
import org.nuxeo.ecm.core.event.EventListener;
import org.nuxeo.ecm.core.event.impl.DocumentEventContext;
import org.nuxeo.runtime.api.Framework;

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
        if (!type.equals(DOCTYPE) || doc.isProxy()) {
            return;
        }

        // Fetch previous version of the document
        DocumentModel previousDoc = (DocumentModel) event.getContext().getProperties().get("previousDocumentModel");
        
        try {

            String url = (String) doc.getProperty(SCHEMA, PROP_URL);
            String fileUrl = (String) doc.getProperty(SCHEMA, PROP_FILEURL);
            
            // Extract information from attached file
            if (fileUrl != null && (previousDoc == null 
                    || isDifferent(SCHEMA_PREFIX + PROP_FILEURL, doc, previousDoc)
                    || doc.getProperty("file:content") == null)) {
                WebFileParsingPoolService webFileParsingPool = Framework.getService(WebFileParsingPoolService.class);
                webFileParsingPool.append(new URL(fileUrl), doc, "file:content", null);
            }
            
            // Maintain properties
            if (url != null) {
                try {
                    String normalizedUrl = PropertyNormalizer.normalizeUrl(url);
                    doc.setProperty(SCHEMA, PROP_URL, normalizedUrl);
                    String urlTemplate = (String) doc.getProperty(SCHEMA, PROP_URLTEMPLATE);
                    if (urlTemplate == null || urlTemplate.isEmpty()) {
                        doc.setProperty(SCHEMA, PROP_URLTEMPLATE, new URL(normalizedUrl).getPath());
                    }
                } catch (MalformedURLException e) {
                    log.warn("Failed to normalize URL", e);
                }
            }
            if (fileUrl != null) {
                // XXX: Hacked Airport Light URL
                if (fileUrl.contains("irport")) {
                    doc.setProperty(SCHEMA, PROP_FILEURL, 
                            "http://localhost:"+EasySOAConstants.HTML_FORM_GENERATOR_PORT
                            +"/scaffoldingProxy/files/modified_airport_soap.wsdl");
                }
                else {
                    doc.setProperty(SCHEMA, PROP_FILEURL, PropertyNormalizer.normalizeUrl(fileUrl));
                }
            }
            String referencedServicePath = (String) doc.getProperty(SCHEMA, PROP_REFERENCESERVICE);
            if (referencedServicePath == null || !session.exists(new PathRef(referencedServicePath))) {
                // If no reference or missing, find new reference by correlation
                DocumentModel newReferenceService = null;
                ServiceValidationService validationService = Framework.getService(ServiceValidationService.class);
                SortedSet<CorrelationMatch> correlatedServices = validationService.findCorrelatedServices(session, doc);
                if (correlatedServices != null && !correlatedServices.isEmpty()
                        && correlatedServices.first().getCorrelationRate() > 0.9) {
                    newReferenceService = correlatedServices.first().getDocumentModel();
                    doc.setProperty(SCHEMA, PROP_REFERENCESERVICE, newReferenceService.getPathAsString());
                    doc.setProperty(SCHEMA, PROP_REFERENCESERVICEORIGIN,
                            "Automatic correlation (" + correlatedServices.first().getCorrelationRateAsPercentageString() + " match)");
                }
                else {
                    doc.setProperty(SCHEMA, PROP_REFERENCESERVICE, null);
                }
            }
            
            // Test if the service already exists, delete the other one(s) if necessary
            try {
                DocumentService docService = Framework.getService(DocumentService.class);
                DocumentModel workspace = docService.getWorkspace(session, doc);
                DocumentModelList existingServiceModels = session.query(
                        "SELECT * FROM " + Service.DOCTYPE + " WHERE" +
                        		" ecm:path STARTSWITH '" + workspace.getPathAsString() + "'" +
                        		" AND " + Service.SCHEMA_PREFIX + Service.PROP_URL + " = '" + url + "'" +
                        		" AND ecm:currentLifeCycleState <> 'deleted'" +
                        		" AND ecm:isProxy = 0");
                for (DocumentModel existingServiceModel : existingServiceModels) {
                    if (existingServiceModel != null && !existingServiceModel.getRef().equals(doc.getRef())
                            && !existingServiceModel.isProxy()) {
                        docService.mergeDocument(session, existingServiceModel, doc, false);
                    }
                }
            } catch (Exception e) {
                log.error("Error while trying to merge documents", e);
            }

        } catch (Exception e) {
            log.error(e);
        }
        

        // Fire validation request:
        // - on creation
        // - if the URL changed
        // - after a notification
        // (TODO Define better when we want to validate a service)
        try {
            if (previousDoc == null
                    || isDifferent(SCHEMA_PREFIX + PROP_URL, doc, previousDoc)
                    || isDifferent(SCHEMA_COMMON_PREFIX + PROP_DTBROWSING, doc, previousDoc)
                    || isDifferent(SCHEMA_COMMON_PREFIX + PROP_DTDESIGN, doc, previousDoc)
                    || isDifferent(SCHEMA_COMMON_PREFIX + PROP_DTIMPORT, doc, previousDoc)
                    || isDifferent(SCHEMA_COMMON_PREFIX + PROP_DTMONITORING, doc, previousDoc)) {
                EventsHelper.fireDocumentEvent(session, EventsHelper.EVENTTYPE_VALIDATIONREQUEST, doc);
            }
        }
        catch (Exception e) {
            log.error("Failed to test ", e);
        }
        

    }

	private boolean isDifferent(String propXPath, DocumentModel doc, DocumentModel previousDoc) throws ClientException {
        Object fromDoc = doc.getPropertyValue(propXPath);
        Object fromPrevDoc = previousDoc.getPropertyValue(propXPath);
	    if (fromDoc == null) {
	        return fromPrevDoc != null;
	    }
	    else {
	        return !fromDoc.equals(fromPrevDoc);
	    }
	}
    
}