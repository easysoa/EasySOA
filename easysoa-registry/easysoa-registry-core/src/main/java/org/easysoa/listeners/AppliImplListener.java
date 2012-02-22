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

import static org.easysoa.doctypes.AppliImpl.DEFAULT_ENVIRONMENT;
import static org.easysoa.doctypes.AppliImpl.DOCTYPE;
import static org.easysoa.doctypes.AppliImpl.PROP_ENVIRONMENT;
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
import org.jboss.seam.core.Events;
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
            Events.instance().raiseEvent(APPLI_IMPL_CHANGED, appliImplModel);
            
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
            
            // Update properties
            if (maintainInternalProperties(session, appliImplModel)) {
                setDefaultPropertyValues(session, appliImplModel);
            }
    
            // Update vocabulary
            try {
                updateVocabulary(session,
                        (String) appliImplModel.getProperty(AppliImpl.SCHEMA, AppliImpl.PROP_SERVER),
                        (String) appliImplModel.getProperty(AppliImpl.SCHEMA, AppliImpl.PROP_ENVIRONMENT));
            } catch (ClientException e) {
                log.error("Failed to fetch Appli. Impl. properties", e);
            }
        }
        
    }

    private boolean maintainInternalProperties(CoreSession session, DocumentModel appliImplModel) {
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
                            appliImplModel.getProperty(SCHEMA, PROP_ENVIRONMENT) + "/" + server);
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

    private boolean setDefaultPropertyValues(CoreSession session, DocumentModel appliImplModel) {
        try { // Default environment
            String environment = (String) appliImplModel.getProperty(SCHEMA, PROP_ENVIRONMENT);
            if (environment == null || environment.isEmpty()) {
                appliImplModel.setProperty(SCHEMA, PROP_ENVIRONMENT, DEFAULT_ENVIRONMENT);
            }
        } catch (Exception e) {
            log.error("Failed to set default environment value", e);
            return false;
        }
        return true;
    }

    private boolean updateVocabulary(CoreSession session, String server, String environment) {
        try {
            // TODO: Update on document deletion
            if (environment == null) {
                environment = DEFAULT_ENVIRONMENT;
            }

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