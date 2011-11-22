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

import static org.easysoa.doctypes.ServiceReference.DOCTYPE;
import static org.easysoa.doctypes.ServiceReference.PROP_REFURL;
import static org.easysoa.doctypes.ServiceReference.SCHEMA;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.easysoa.api.EasySOAApiSession;
import org.easysoa.api.EasySOALocalApiFactory;
import org.easysoa.doctypes.Service;
import org.easysoa.services.DocumentService;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.event.Event;
import org.nuxeo.ecm.core.event.EventContext;
import org.nuxeo.ecm.core.event.EventListener;
import org.nuxeo.ecm.core.event.impl.DocumentEventContext;
import org.nuxeo.runtime.api.Framework;

/**
 * 
 * @author mdutoo
 *
 */
public class ServiceReferenceListener implements EventListener {
    
    private static Log log = LogFactory.getLog(ServiceReferenceListener.class);

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

            DocumentService docService = Framework.getService(DocumentService.class);
            EasySOAApiSession api = EasySOALocalApiFactory.createLocalApi(session);
            
            // Create service from WSDL if it doesn't exist
            String refUrl = (String) doc.getProperty(SCHEMA, PROP_REFURL);
            if (refUrl != null && !refUrl.isEmpty()) {
                DocumentModel referenceService = docService.findService(session, refUrl);
                if (referenceService == null) {
                    Map<String, String> properties = new HashMap<String, String>();
                    properties.put(Service.PROP_URL, refUrl);
                    api.notifyService(properties);
                }
            }
            
            session.save();
            
        } catch (Exception e) {
            log.error("Error while parsing WSDL", e);
        }
        
    }
    
}