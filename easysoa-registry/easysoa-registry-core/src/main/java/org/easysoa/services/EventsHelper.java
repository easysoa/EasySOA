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

package org.easysoa.services;

import java.io.Serializable;
import java.util.Map;

import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.event.EventProducer;
import org.nuxeo.ecm.core.event.impl.DocumentEventContext;
import org.nuxeo.runtime.api.Framework;
import org.nuxeo.runtime.model.DefaultComponent;
import org.nuxeo.ecm.core.event.Event;

/**
 * Helpers for sending events.
 * 
 * @author mkalam-alami
 *
 */
public class EventsHelper extends DefaultComponent {

    public final static String EVENTTYPE_VALIDATIONREQUEST = "documentValidationRequest";
    
    public static void fireDocumentEvent(CoreSession session, String eventId, DocumentModel doc, Map<String, Serializable> context) throws Exception {
        fireEvent(session, eventId, doc, context);
    }

    public static void fireDocumentEvent(CoreSession session, String eventId, DocumentModel doc) throws Exception {
        fireEvent(session, eventId, doc, null);
    }
    
    public static void fireEvent(CoreSession session, String eventId) throws Exception {
        fireEvent(session, eventId, null, null);
    }

    public static void fireEvent(CoreSession session, String eventId, Map<String, Serializable> context) throws Exception {
        fireEvent(session, eventId, null, context);
    }
    
    private static void fireEvent(CoreSession session, String eventId, DocumentModel doc, Map<String, Serializable> context) throws Exception {
        EventProducer eventProducer = Framework.getService(EventProducer.class);
        DocumentEventContext ctx = new DocumentEventContext(session, session.getPrincipal(), doc);
        ctx.setProperties(context);
        Event event = ctx.newEvent(eventId);
        eventProducer.fireEvent(event);
    }
    
}