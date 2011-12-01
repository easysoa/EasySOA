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

package org.easysoa.environments;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.easysoa.doctypes.Service;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.event.DocumentEventTypes;
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
public class PublicationListener implements EventListener {

    private static Log log = LogFactory.getLog(PublicationListener.class);

    private PublicationService publisher;

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

        // XXX Just a publication POC
        if (type.equals(Service.DOCTYPE) && !doc.isProxy()) {
            
            // Publish
            if (!DocumentEventTypes.ABOUT_TO_REMOVE.equals(event.getName())) {

                try {
                    // Init publication
                    if (publisher == null) {
                        publisher = Framework.getService(PublicationService.class);
                    }
                    String url = (String) doc.getProperty(Service.SCHEMA, Service.PROP_URL);
                    
                    // Publish according to URL
                    if (url != null && !url.isEmpty()) {
                        if (url.contains("127.0.0.1")) {
                            publisher.publish(session, doc, "Development");
                        } else {
                            publisher.publish(session, doc, "Master");
                        }
                    }
                } catch (Exception e) {
                    log.error("Failed to publish service", e);
                }
            }
            
            // Unpublish
            else {
                publisher.unpublish(session, doc);
            }

        }

    }

}