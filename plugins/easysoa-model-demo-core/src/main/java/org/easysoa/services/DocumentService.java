package org.easysoa.services;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.easysoa.doctypes.AppliImpl;
import org.easysoa.doctypes.Service;
import org.easysoa.doctypes.ServiceAPI;
import org.nuxeo.common.utils.IdUtils;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentModelList;
import org.nuxeo.ecm.core.api.DocumentRef;
import org.nuxeo.ecm.core.api.PathRef;

/**
 * 
 * @author mkalam-alami
 *
 */
// TODO: Switch to real Nuxeo service instead of static access
public class DocumentService {
	
	@SuppressWarnings("unused")
	private static final Log log = LogFactory.getLog(DocumentService.class);
	
	public static final String DOMAIN_TITLE = "EasySOA";
	public static final String WORKSPACE_ROOT_TITLE = "Service Registry";
	public static final String DEFAULT_APPLIIMPL_TITLE = "Default application";
	public static final String DEFAULT_APPLIIMPL_URL = "(Unknown)";

	// Must not be directly accessed, use getters
	private static DocumentModel defaultAppliImpl = null;
	private static DocumentModel wsRoot = null; 
	
	public static final DocumentModel createAppliImpl(CoreSession session, String url) throws ClientException {
		
		DocumentModel appliImpl = session.createDocumentModel(
				getWSRoot(session).getPathAsString(),
				IdUtils.generateStringId(),
				AppliImpl.DOCTYPE);
		appliImpl.setProperty(AppliImpl.SCHEMA, AppliImpl.PROP_URL, url);
		appliImpl.setProperty("dublincore", "title", url);
		return session.createDocument(appliImpl);
	}

	/**
	 * 
	 * @param session
	 * @param parentPath If null or invalid, default application is used
	 * @param title
	 * @return
	 * @throws ClientException
	 */
	public static final DocumentModel createServiceAPI(CoreSession session,
			String parentPath, String url) throws ClientException {
		
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

		DocumentModel serviceAPI = session.createDocumentModel(
				parentPath, IdUtils.generateStringId(), ServiceAPI.DOCTYPE);
		serviceAPI.setProperty(ServiceAPI.SCHEMA, ServiceAPI.PROP_URL, url);
		serviceAPI.setProperty("dublincore", "title", url);
		return session.createDocument(serviceAPI);
	}
	
	/**
	 * Returns null if the service API doesn't exist.
	 * @param session
	 * @param apiTitle
	 * @param title
	 * @return
	 * @throws ClientException
	 */
	public static final DocumentModel createService(CoreSession session,
			String parentPath, String url) throws ClientException {
		
		if (parentPath != null) {
			DocumentModel service = session.createDocumentModel(
					parentPath, IdUtils.generateStringId(), Service.DOCTYPE);
			if (service != null) {
				service.setProperty(Service.SCHEMA, Service.PROP_URL, url);
				service.setProperty("dublincore", "title", url);
			}
			return session.createDocument(service);
		}
		else {
			return null;
		}
	}
	
	/**
	 * Returns the default Appli Impl., creates it if necessary.
	 * @param session
	 * @return
	 * @throws ClientException
	 */
	public static DocumentModel getDefaultAppliImpl(CoreSession session) throws ClientException {
		
		if (defaultAppliImpl == null || !session.exists(defaultAppliImpl.getRef())) {
			DocumentModel appliimpl = DocumentService.getChild(session, getWSRoot(session).getRef(), DEFAULT_APPLIIMPL_TITLE);
			if (appliimpl == null) {
				DocumentModel appliImpl = createAppliImpl(session, DEFAULT_APPLIIMPL_URL);
				appliImpl.setProperty("dublincore", "title", DEFAULT_APPLIIMPL_TITLE);
				session.saveDocument(appliImpl);
				session.save();
				defaultAppliImpl = appliImpl;
				return defaultAppliImpl;
			}
			else
				defaultAppliImpl = appliimpl;
		}
		return defaultAppliImpl;
	}
	
	public static DocumentModel findAppliImpl(CoreSession session, String appliUrl) throws ClientException {
		if (appliUrl == null)
			return null;
		return findFirstDocument(session, AppliImpl.DOCTYPE, 
				AppliImpl.SCHEMA_PREFIX+AppliImpl.PROP_URL, appliUrl);
	}
	
	public static DocumentModel findServiceApi(CoreSession session, String apiUrl) throws ClientException {
		if (apiUrl == null)
			return null;
		return findFirstDocument(session, ServiceAPI.DOCTYPE, 
				ServiceAPI.SCHEMA_PREFIX+ServiceAPI.PROP_URL, apiUrl);
	}
	
	public static DocumentModel findService(CoreSession session, String serviceUrl) throws ClientException {
		if (serviceUrl == null)
			return null;
		return findFirstDocument(session, Service.DOCTYPE,
				Service.SCHEMA_PREFIX+Service.PROP_URL, serviceUrl);
	}

	private static DocumentModel findFirstDocument(CoreSession session, String type, String field, String value) throws ClientException {
		DocumentModelList apis = session.query("SELECT * FROM Document WHERE ecm:primaryType = '" + 
				type + "' AND " + field + " = '" +  value + "' AND ecm:currentLifeCycleState <> 'deleted'");
		return (apis != null && apis.size() > 0) ? apis.get(0) : null;
	}

	private static DocumentModel getWSRoot(CoreSession session) throws ClientException {
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

	private static DocumentModel getChild(CoreSession session, DocumentRef parent, String childTitle) throws ClientException { 
		for (DocumentModel model : session.getChildren(parent)) {
			if (model.getTitle().equals(childTitle) && model.getCurrentLifeCycleState() != "deleted") {
				return model;
			}
		}
		return null;
	}


}