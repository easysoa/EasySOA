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
import org.easysoa.services.DocumentService;
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
public class DocumentServiceImpl extends DefaultComponent implements DocumentService {

    private static Log log = LogFactory.getLog(DocumentServiceImpl.class);

    private final static String DEFAULT_WORKSPACE = "Master";

    private PathRef workspaceRootRef = new PathRef("/default-domain/workspaces");

    public final DocumentModel createAppliImpl(CoreSession session, String url) throws ClientException, MalformedURLException {
        return createAppliImpl(session, url, DEFAULT_WORKSPACE);
    }

    public final DocumentModel createAppliImpl(CoreSession session, String url, String workspace) throws ClientException, MalformedURLException {
        // Find or create workspace
        DocumentModel workspaceModel = findWorkspace(session, workspace);
        if (workspaceModel == null) {
            workspaceModel = session.createDocumentModel(workspaceRootRef.toString(), IdUtils.generateId(workspace, "", true, 15), "Workspace");
            workspaceModel.setProperty("dublincore", "title", workspace);
            workspaceModel = session.createDocument(workspaceModel);
            session.save();
        }
        // Create Appli Impl.
        String normalizedUrl = PropertyNormalizer.normalizeUrl(url);
        DocumentModel appliImplModel = session.createDocumentModel(AppliImpl.DOCTYPE);
        appliImplModel.setProperty(AppliImpl.SCHEMA, AppliImpl.PROP_URL, normalizedUrl);
        appliImplModel.setProperty("dublincore", "title", normalizedUrl);
        appliImplModel.setPathInfo(workspaceModel.getPathAsString(), generateDocumentID(appliImplModel));
        return session.createDocument(appliImplModel);
    }

    public final DocumentModel createServiceAPI(CoreSession session, String parentPath, String url) throws ClientException, MalformedURLException {
        String normalizedUrl = PropertyNormalizer.normalizeUrl(url);
        boolean invalidParent = false;
        if (parentPath == null) {
            invalidParent = true;
        } else if (!session.exists(new PathRef(parentPath))) {
            log.warn("Parent AppliImpl " + parentPath + " not found, using default");
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

    public final DocumentModel createService(CoreSession session, String parentPath, String url) throws ClientException, MalformedURLException {
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
        } else {
            return null;
        }
    }

    public final DocumentModel createReference(CoreSession session, String parentPath, String title) throws ClientException {
        if (parentPath != null) {
            DocumentModel referenceModel = session.createDocumentModel(ServiceReference.DOCTYPE);
            referenceModel.setProperty("dublincore", "title", title);
            referenceModel.setPathInfo(parentPath, generateDocumentID(referenceModel));
            return session.createDocument(referenceModel);
        } else {
            return null;
        }
    }

    public DocumentModel findWorkspace(CoreSession session, String name) throws ClientException {
        if (name != null) {
            return findFirstDocument(session, "Workspace", "dc:title", name);
        } else {
            return null;
        }
    }
    
    public DocumentModel findEnvironment(CoreSession session, String name) throws ClientException {
        if (name != null) {
            return findFirstDocument(session, "Section", "dc:title", name);
        } else {
            return null;
        }
    }

    public DocumentModel findAppliImpl(CoreSession session, String appliUrl) throws ClientException {
        if (appliUrl != null) {
            try {
                return findFirstDocument(session, AppliImpl.DOCTYPE, AppliImpl.SCHEMA_PREFIX + AppliImpl.PROP_URL, PropertyNormalizer.normalizeUrl(appliUrl));
            } catch (MalformedURLException e) {
                return null;
            }
        } else {
            return null;
        }
    }

    public DocumentModel findServiceApi(CoreSession session, String apiUrl) throws ClientException {
        if (apiUrl != null) {
            try {
                return findFirstDocument(session, ServiceAPI.DOCTYPE, ServiceAPI.SCHEMA_PREFIX + ServiceAPI.PROP_URL, PropertyNormalizer.normalizeUrl(apiUrl));
            } catch (MalformedURLException e) {
                return null;
            }
        } else {
            return null;
        }
    }

    public DocumentModel findService(CoreSession session, String url) throws ClientException, MalformedURLException {
        if (url != null) {
            DocumentModel result = findFirstDocument(session, Service.DOCTYPE, Service.SCHEMA_PREFIX + Service.PROP_URL, PropertyNormalizer.normalizeUrl(url));
            if (result == null) {
                // Match either service url or WSDL url
                result = findFirstDocument(session, Service.DOCTYPE, Service.SCHEMA_PREFIX + Service.PROP_FILEURL, url);
            }
            return result;
        } else {
            return null;
        }
    }

    public DocumentModel findServiceReference(CoreSession session, String referenceArchiPath) throws ClientException {
        if (referenceArchiPath == null) {
            return null;
        }
        return findFirstDocument(session, ServiceReference.DOCTYPE, EasySOADoctype.SCHEMA_COMMON_PREFIX + ServiceReference.PROP_ARCHIPATH, referenceArchiPath);
    }

    public boolean mergeDocument(CoreSession session, DocumentModel from, DocumentModel to, boolean overwrite) throws ClientException {
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
        } else {
            return false;
        }
    }

    public String generateDocumentID(DocumentModel doc) {
        try {
            return IdUtils.generateId(doc.getTitle(), "-", true, 0);
        } catch (Exception e) {
            return IdUtils.generateStringId();
        }
    }

    public DocumentModel getDefaultAppliImpl(CoreSession session) throws ClientException {
    	return getDefaultAppliImpl(session, DEFAULT_WORKSPACE);
    }
    
    public DocumentModel getDefaultAppliImpl(CoreSession session, String workspace) throws ClientException {
    	
    	// Find the AppliImpl
    	if (workspace == null) {
    		workspace = DEFAULT_WORKSPACE;
    	}
    	DocumentModel workspaceModel = findWorkspace(session, workspace);
    	DocumentModel appliImpl = null;
    	if (workspaceModel != null) {
            DocumentModelList appliImplList = session.query("SELECT * FROM " + AppliImpl.DOCTYPE + 
            		" WHERE " + AppliImpl.SCHEMA_PREFIX + AppliImpl.PROP_URL + " = '" + AppliImpl.DEFAULT_APPLIIMPL_URL + 
            		"' AND ecm:path STARTSWITH '" + workspaceModel.getPathAsString() + "'");
            if (appliImplList != null && !appliImplList.isEmpty()) {
            	appliImpl = appliImplList.get(0);
            }
    	}
    	
    	// If not found, create it
        if (appliImpl == null) {
            try {
                appliImpl = createAppliImpl(session, AppliImpl.DEFAULT_APPLIIMPL_URL, workspace);
                appliImpl.setProperty("dublincore", "title", AppliImpl.DEFAULT_APPLIIMPL_TITLE);
                session.saveDocument(appliImpl);
                session.save();
                return appliImpl;
            } catch (MalformedURLException e) {
                log.error("Default Appli Impl. URL is invalid", e);
                return null;
            }
        } else {
            return appliImpl;
        }
    }

    public DocumentModel getWorkspace(CoreSession session, DocumentModel model) throws ClientException {
        // Use path to retrieve the workspace's path
        String path = model.getPathAsString() + "/";
        int i = 0;
        for (int step = 1; step <= 3; step++) {
            i = path.indexOf('/', i + 1);
        }
        if (i != -1) {
            // Retrieve and check workspace
            DocumentModel workspace = session.getDocument(new PathRef(path.substring(0, i)));
            if (workspace != null && workspace.getType().equals("Workspace")) {
                return workspace;
            } else {
                return null;
            }
        } else {
            return null;
        }
    }
    
    public DocumentRef getWorkspaceRoot(CoreSession session) throws ClientException {
        return workspaceRootRef;
    }

    private DocumentModel findFirstDocument(CoreSession session, String type, String field, String value) throws ClientException {
        DocumentModelList apis = session.query("SELECT * FROM " + type + " WHERE " + 
        		field + " = '" + value + "' AND ecm:currentLifeCycleState <> 'deleted' AND ecm:isProxy = 0");
        return (apis != null && !apis.isEmpty()) ? apis.get(0) : null;
    }

}