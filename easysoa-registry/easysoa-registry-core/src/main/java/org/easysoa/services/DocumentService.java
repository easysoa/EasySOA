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
 * Contact : easysoa-dev@groups.google.com
 */

package org.easysoa.services;

import java.io.Serializable;
import java.net.MalformedURLException;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.easysoa.doctypes.AppliImpl;
import org.easysoa.doctypes.EasySOADoctype;
import org.easysoa.doctypes.Service;
import org.easysoa.doctypes.ServiceAPI;
import org.easysoa.doctypes.ServiceReference;
import org.easysoa.properties.PropertyNormalizer;
import org.nuxeo.common.utils.IdUtils;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentModelList;
import org.nuxeo.ecm.core.api.DocumentRef;
import org.nuxeo.ecm.core.api.PathRef;
import org.nuxeo.runtime.model.DefaultComponent;

/**
 * 
 * @author mkalam-alami
 *
 */
public class DocumentService extends DefaultComponent {
    
    private static Log log = LogFactory.getLog(DocumentService.class);

    // Must not be directly accessed, use getters
    private DocumentModel defaultAppliImpl = null;
    private DocumentModel wsRoot = null; 
    
    public final DocumentModel createAppliImpl(CoreSession session, String url) throws ClientException, MalformedURLException {
        String normalizedUrl = PropertyNormalizer.normalizeUrl(url);
        DocumentModel appliImplModel = session.createDocumentModel(AppliImpl.DOCTYPE);
        appliImplModel.setProperty(AppliImpl.SCHEMA, AppliImpl.PROP_URL, normalizedUrl);
        appliImplModel.setProperty("dublincore", "title", normalizedUrl);
        appliImplModel.setPathInfo(getWorkspaceRoot(session).getPathAsString(), generateDocumentID(appliImplModel));
        return session.createDocument(appliImplModel);
    }

    /**
     * 
     * @param session
     * @param parentPath If null or invalid, default application is used
     * @param url
     * @return
     * @throws ClientException
     * @throws MalformedURLException 
     */
    public final DocumentModel createServiceAPI(CoreSession session,
            String parentPath, String url) throws ClientException, MalformedURLException {
        String normalizedUrl = PropertyNormalizer.normalizeUrl(url);
        boolean invalidParent = false;
        if (parentPath == null) {
            invalidParent = true;
        }
        else if (!session.exists(new PathRef(parentPath))) {
            log.warn("Parent AppliImpl "+parentPath+" not found, using default");
            invalidParent = true;
        }
        if (invalidParent) {
            parentPath = session.getDocument(getDefaultAppliImpl(session).getRef()).getPathAsString();
        }

        DocumentModel apiModel = session.createDocumentModel(ServiceAPI.DOCTYPE);
        apiModel.setProperty(ServiceAPI.SCHEMA, ServiceAPI.PROP_URL, normalizedUrl);
        apiModel.setProperty("dublincore", "title", normalizedUrl);
        apiModel.setPathInfo(parentPath, generateDocumentID(apiModel));
        return session.createDocument(apiModel);
    }
    
    /**
     * Returns null if the service API doesn't exist.
     * 
     * @param session
     * @param parentPath service API
     * @param url
     * @return
     * @throws ClientException
     * @throws MalformedURLException 
     */
    public final DocumentModel createService(CoreSession session,
            String parentPath, String url) throws ClientException, MalformedURLException {
        if (parentPath != null) {
            String normalizedUrl = PropertyNormalizer.normalizeUrl(url);
            DocumentModel serviceModel = session.createDocumentModel(Service.DOCTYPE);
            serviceModel.setProperty(Service.SCHEMA, Service.PROP_URL, normalizedUrl);
            if (normalizedUrl.toLowerCase().contains("wsdl")) {
                serviceModel.setProperty(Service.SCHEMA, Service.PROP_FILEURL, normalizedUrl);
            }
            serviceModel.setProperty("dublincore", "title", normalizedUrl);
            serviceModel.setPathInfo(parentPath, generateDocumentID(serviceModel));
            return session.createDocument(serviceModel);
        }
        else {
            return null;
        }
    }
    
    /**
     * Returns null if the service API Impl doesn't exist.
     * 
     * @param session
     * @param parentPath service API Impl
     * @param archiPath
     * @return
     * @throws ClientException
     */
    public final DocumentModel createReference(CoreSession session,
            String parentPath, String title) throws ClientException {
        if (parentPath != null) {
            DocumentModel referenceModel = session.createDocumentModel(ServiceReference.DOCTYPE);
            referenceModel.setProperty("dublincore", "title", title);
            referenceModel.setPathInfo(parentPath, generateDocumentID(referenceModel));
            return session.createDocument(referenceModel);
        }
        else {
            return null;
        }
    }
    
    public DocumentModel findAppliImpl(CoreSession session, String appliUrl) throws ClientException {
        if (appliUrl != null) {
            try {
                return findFirstDocument(session, AppliImpl.DOCTYPE, 
                        AppliImpl.SCHEMA_PREFIX+AppliImpl.PROP_URL,
                        PropertyNormalizer.normalizeUrl(appliUrl));
            } catch (MalformedURLException e) {
                return null;
            }
        }
        else {
            return null;
        }
    }
    
    public DocumentModel findServiceApi(CoreSession session, String apiUrl) throws ClientException {
        if (apiUrl != null) {
            try {
                return findFirstDocument(session, ServiceAPI.DOCTYPE, 
                        ServiceAPI.SCHEMA_PREFIX+ServiceAPI.PROP_URL,
                        PropertyNormalizer.normalizeUrl(apiUrl));
            } catch (MalformedURLException e) {
                return null;
            }
        }
        else {
            return null;
        }
    }
    
    public DocumentModel findService(CoreSession session, String url) throws ClientException, MalformedURLException {
        if (url != null) {
            DocumentModel result = findFirstDocument(session, Service.DOCTYPE,
                    Service.SCHEMA_PREFIX+Service.PROP_URL, PropertyNormalizer.normalizeUrl(url));
            if (result == null) {
                // Match either service url or WSDL url
                result = findFirstDocument(session, Service.DOCTYPE,
                        Service.SCHEMA_PREFIX+Service.PROP_FILEURL, url);
            }
            return result;
        }
        else {
            return null;
        }
    }

    public DocumentModel findServiceReference(CoreSession session,
            String referenceArchiPath) throws ClientException {
        if (referenceArchiPath == null) {
            return null;
        }
        return findFirstDocument(session, ServiceReference.DOCTYPE,
                EasySOADoctype.SCHEMA_COMMON_PREFIX +ServiceReference.PROP_ARCHIPATH, referenceArchiPath);
    }
    
    /**
     * Merges properties from a document to another,
     * i.e. copies properties from a source model to the destination.
     * The source document is deleted, and the destination saved.
     * @param from
     * @param to
     * @param overwrite If destination properties have to be overwritten
     * @return
     * @throws ClientException
     */
    public boolean mergeDocument(CoreSession session, DocumentModel from,
            DocumentModel to, boolean overwrite) throws ClientException {
        if (to.getType().equals(to.getType())) {
            for (String schema : from.getDocumentType().getSchemaNames()) {
                Map<String, Object> schemaPropertiesFrom = from.getProperties(schema);
                Map<String, Object> schemaPropertiesTo = to.getProperties(schema);
                for (Entry<String, Object> entry : schemaPropertiesFrom.entrySet()) {
                    String property = entry.getKey();
                    Serializable fromValue = (Serializable) entry.getValue();
                    if (fromValue != null && (schemaPropertiesTo.get(property) == null || overwrite)) {
                        to.setPropertyValue(property, fromValue);
                    }
                }
            }
            session.removeDocument(from.getRef());
            return true;
        }
        else {
            return false;
        }
    }

    public String generateDocumentID(DocumentModel doc) {
        try {
            return IdUtils.generateId(doc.getTitle(), "-", true, 0);
        }
        catch (Exception e) {
            return IdUtils.generateStringId();
        }
    }

    /**
     * Returns the default Appli Impl., creates it if necessary.
     * @param session
     * @return
     * @throws ClientException
     */
    public DocumentModel getDefaultAppliImpl(CoreSession session) throws ClientException {
        if (defaultAppliImpl == null || !session.exists(defaultAppliImpl.getRef())) {
            DocumentModel appliimpl = getChild(session, getWorkspaceRoot(session).getRef(), AppliImpl.DEFAULT_APPLIIMPL_TITLE);
            if (appliimpl == null) {
                DocumentModel appliImpl;
                try {
                    appliImpl = createAppliImpl(session, AppliImpl.DEFAULT_APPLIIMPL_URL);
                    appliImpl.setProperty("dublincore", "title", AppliImpl.DEFAULT_APPLIIMPL_TITLE);
                    appliImpl.setProperty(AppliImpl.SCHEMA, AppliImpl.PROP_ENVIRONMENT,
                            AppliImpl.DEFAULT_ENVIRONMENT);
                    session.saveDocument(appliImpl);
                    session.save();
                    defaultAppliImpl = appliImpl;
                    return defaultAppliImpl;
                } catch (MalformedURLException e) {
                    log.error("Default Appli Impl. URL is invalid", e);
                    return null;
                }
            }
            else
                defaultAppliImpl = appliimpl;
        }
        return defaultAppliImpl;
    }

    public DocumentModel getWorkspaceRoot(CoreSession session) throws ClientException {
        if (wsRoot == null || !session.exists(wsRoot.getRef())) {
            DocumentModel defaultDomain = session.getChildren(session.getRootDocument().getRef()).get(0);
            DocumentModelList domainChildren =  session.getChildren(defaultDomain.getRef());
            for (DocumentModel model : domainChildren) {
                if (model.getType().equals("WorkspaceRoot")) {
                    return model;
                }
            }
        }
        return wsRoot;
    }

    private DocumentModel findFirstDocument(CoreSession session, String type, String field, String value) throws ClientException {
        DocumentModelList apis = session.query("SELECT * FROM Document WHERE ecm:primaryType = '" + 
                type + "' AND " + field + " = '" +  value + "' AND ecm:currentLifeCycleState <> 'deleted'");
        return (apis != null && apis.size() > 0) ? apis.get(0) : null;
    }

    private DocumentModel getChild(CoreSession session, DocumentRef parent, String childTitle) throws ClientException { 
        for (DocumentModel model : session.getChildren(parent)) {
            if (model.getTitle().equals(childTitle) && model.getCurrentLifeCycleState() != "deleted") {
                return model;
            }
        }
        return null;
    }

}