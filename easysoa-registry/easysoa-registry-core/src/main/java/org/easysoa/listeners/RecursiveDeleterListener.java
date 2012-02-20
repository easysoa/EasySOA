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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentModelList;
import org.nuxeo.ecm.core.event.Event;
import org.nuxeo.ecm.core.event.EventContext;
import org.nuxeo.ecm.core.event.EventListener;
import org.nuxeo.ecm.core.event.impl.DocumentEventContext;

/**
 * 
 * @author mdutoo
 *
 */
public class RecursiveDeleterListener implements EventListener {
    
    private static Log log = LogFactory.getLog(RecursiveDeleterListener.class);

    public void handleEvent(Event event) {

        // Init
        EventContext context = event.getContext();
        CoreSession session =  context.getCoreSession();
        DocumentModel doc = ((DocumentEventContext) context).getSourceDocument();

        // Delete recursively
        try {
            if (doc.getCurrentLifeCycleState().equals("deleted")) {
                
//                Deleting doesn't really help, since it seems like Nuxeo makes a
//                not-deleted copy of the document at the same time (why?).
                
//                DocumentModelList children = session.getChildren(doc.getRef());
//                for (DocumentModel child : children) {
//                    child.followTransition("delete");
//                    session.saveDocument(child);
//                }
                
                // XXX Find nicer way to get deleted documents out of the way of the correlation alg.
                session.removeChildren(doc.getRef());
                
            }
        } catch (ClientException e) {
            log.error("Failed to delete recursively", e);
        }   
        
        try {
            session.save();
        } catch (ClientException e) {
            log.error("Failed to save", e);
        }
        
    }
    
}