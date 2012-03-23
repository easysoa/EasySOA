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
import org.easysoa.doctypes.Workspace;
import org.easysoa.services.DocumentService;
import org.easysoa.services.ServiceValidationService;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
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
public class ValidationListener implements EventListener {

    private static Log log = LogFactory.getLog(ValidationListener.class);

    public void handleEvent(Event event) {

        // Init
        EventContext context = event.getContext();
        CoreSession session =  context.getCoreSession();
        DocumentModel doc = ((DocumentEventContext) context).getSourceDocument();

        try {
            // Find workspace
            DocumentService docService = Framework.getService(DocumentService.class);
            DocumentModel workspace = docService.getWorkspace(session, doc);

            ServiceValidationService validationService = Framework.getService(ServiceValidationService.class);
            boolean hasReferenceEnvironment = workspace.getProperty(Workspace.SCHEMA, Workspace.PROP_REFERENCEDENVIRONMENT) != null;

            // Run validation
            if (hasReferenceEnvironment) {
		        Boolean wasValidated = (Boolean) workspace.getProperty(Workspace.SCHEMA, Workspace.PROP_ISVALIDATED);
		        boolean isValidated;
		        if (wasValidated != null && wasValidated) {
		            // Validate all child services
		        	isValidated = validationService.validateServices(session, doc).isEveryValidationPassed();
		        } else {
		            // If the environment was not successfully validated,
		            // re-check all to allow it to become validated
		        	isValidated = validationService.validateServices(session, workspace).isEveryValidationPassed();
		        }
		
		        // Update workspace state
		        if (wasValidated == null || wasValidated != isValidated) {
			        workspace.setProperty(Workspace.SCHEMA, Workspace.PROP_ISVALIDATED, isValidated);
			        session.saveDocument(workspace);
		        }
		        
		        // Save workspace & all modified services validation states
		        session.save();
		    }
            
        } catch (Exception e) {
            log.error("Failed to validate " + doc.getType(), e);
        }

    }
    
}