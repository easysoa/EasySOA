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

import static org.easysoa.doctypes.AppliImpl.DOCTYPE;
import static org.easysoa.doctypes.AppliImpl.PROP_SERVER;
import static org.easysoa.doctypes.AppliImpl.PROP_SERVERENTRY;
import static org.easysoa.doctypes.AppliImpl.PROP_URL;
import static org.easysoa.doctypes.AppliImpl.SCHEMA;

import java.net.MalformedURLException;
import java.net.URL;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.easysoa.doctypes.AppliImpl;
import org.easysoa.properties.PropertyNormalizer;
import org.easysoa.services.ServicesRootMapperService;
import org.easysoa.services.VocabularyHelper;
import org.nuxeo.ecm.core.api.ClientException;
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
public class AppliImplListener implements EventListener {

    public static final String APPLI_IMPL_CHANGED = "appliImplChanged";

    private static Log log = LogFactory.getLog(AppliImplListener.class);

    public void handleEvent(Event event) {

        // Check event and retrieve data
        EventContext ctx = event.getContext();
        if (!(ctx instanceof DocumentEventContext)) {
            return;
        }
        CoreSession session = ctx.getCoreSession();

        DocumentModel appliImplModel = ((DocumentEventContext) ctx).getSourceDocument();
        if (appliImplModel == null) {
            return;
        }
        String type = appliImplModel.getType();
        if (!type.equals(DOCTYPE)) {
            return;
        }

        if (event.getName().equals(DocumentEventTypes.DOCUMENT_UPDATED)) {
            // Allow beans to update after appli impl. change

            // Removed : this does not make sense
            // Seam events are request scoped and there is no reason CoreEvent should occurs in the same thread / context
            // => this can not work for real !!!
            //Events.instance().raiseEvent(APPLI_IMPL_CHANGED, appliImplModel);

            // Trigger URL mapper
            try {
                ServicesRootMapperService urlMapper = Framework.getService(ServicesRootMapperService.class);
                urlMapper.mapUrls(session, appliImplModel);
            } catch (Exception e) {
                log.warn("Failed to map URLs of AppliImpl " + appliImplModel.getPathAsString(), e);
            }
        }
        // On document creation / Before modification
        else {
        	try {
	            DocumentModel parentModel = session.getDocument(appliImplModel.getParentRef());
	        	String environment = parentModel.getTitle();
	        	
	            // Update vocabulary
	            updateVocabulary(session,
	                        (String) appliImplModel.getProperty(AppliImpl.SCHEMA, AppliImpl.PROP_SERVER),
	                        parentModel.getTitle());
	            
	            // Update properties
	        	maintainInternalProperties(session, appliImplModel, environment);
        	}
        	catch (Exception e) {
        		log.error("Failed to maintain internal AppliImpl. properties", e);
        	}
        }

    }

    private boolean maintainInternalProperties(CoreSession session, DocumentModel appliImplModel, String environment) {
        try {
            String url = (String) appliImplModel.getProperty(SCHEMA, PROP_URL);
            String server = (String) appliImplModel.getProperty(SCHEMA,
                    PROP_SERVER);

            // Maintain internal properties
            if (url != null && !url.isEmpty() && (server == null || server.isEmpty())) {
                try {
                    url = PropertyNormalizer.normalizeUrl(url);
                    appliImplModel.setProperty(SCHEMA, PROP_URL, url);
                    server = new URL(url).getHost();
                    appliImplModel.setProperty(SCHEMA, PROP_SERVER, server);
                    appliImplModel.setProperty(
                            SCHEMA, PROP_SERVERENTRY, // Internal (for virtual navigation)
                            environment + "/" + server);
                } catch (MalformedURLException e) {
                    log.warn("Failed to normalize URL '" + url + "'");
                }
            }
        } catch (Exception e) {
            log.error("Failed to maintain internal properties", e);
            return false;
        }
        return true;

    }

    private boolean updateVocabulary(CoreSession session, String server, String environment) {
        try {

            VocabularyHelper vocService = Framework.getRuntime().getService(VocabularyHelper.class);

            if (!vocService.entryExists(session,
                    VocabularyHelper.VOCABULARY_ENVIRONMENT, environment)) {
                vocService.addEntry(session, VocabularyHelper.VOCABULARY_ENVIRONMENT,
                        environment, environment);
            }
            if (server != null && !server.isEmpty()
                    && !vocService.entryExists(session, VocabularyHelper.VOCABULARY_SERVER, server)) {
                vocService.addEntry(session, VocabularyHelper.VOCABULARY_SERVER,
                        server, server, environment);
            }
        } catch (ClientException e) {
            log.error("Failed to update vocabulary", e);
            return false;
        }
        return true;
    }

}