package org.easysoa.services;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentModelList;
import org.nuxeo.ecm.core.api.DocumentRef;

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

	public static final String APPLIIMPL_DOCTYPE = "Workspace";
	public static final String SERVICEAPI_DOCTYPE = "ServiceAPI";
	public static final String SERVICE_DOCTYPE = "Service";

	// Must not be directly accessed, use getters
	private static DocumentModel defaultAppliImpl = null;
	private static DocumentModel wsRoot = null; 
	
	public static final DocumentModel createAppliImpl(CoreSession session, String title) throws ClientException {
		
		DocumentModel appliImpl = session.createDocumentModel(APPLIIMPL_DOCTYPE);
		appliImpl.setPathInfo(getWSRoot(session).getPathAsString(), appliImpl.getName());
		appliImpl.setProperty("dublincore", "title", title);
		session.createDocument(appliImpl);
		return appliImpl;
	}

	public static final DocumentModel createServiceAPI(CoreSession session, String parentURL, String title) throws ClientException {
		
		DocumentModel parentModel = DocumentService.findServiceApi(session, parentURL);
		if (parentModel == null) {
			parentModel = DocumentService.findAppliImpl(session, parentURL);
		}
		if (parentModel == null) {
			parentModel = DocumentService.createAppliImpl(session, parentURL);
			parentModel.setProperty("appliimpldef", "rootServicesUrl", parentURL);
			session.saveDocument(parentModel);
		}
		if (parentModel == null) {
			parentModel = session.getDocument(getDefaultAppliImpl(session).getRef());
		}

		DocumentModel serviceAPI = session.createDocumentModel(SERVICEAPI_DOCTYPE);
		serviceAPI.setPathInfo(parentModel.getPathAsString(), serviceAPI.getName());
		serviceAPI.setProperty("dublincore", "title", title);
		session.createDocument(serviceAPI);
		return serviceAPI;
	}
	
	/**
	 * Returns null if the service doesn't exist.
	 * @param session
	 * @param apiTitle
	 * @param title
	 * @return
	 * @throws ClientException
	 */
	public static final DocumentModel createService(CoreSession session, String apiUrl, String title) throws ClientException {
		
		DocumentModel api = findServiceApi(session, apiUrl);
		
		if (api != null) {
			DocumentModel service = session.createDocumentModel(SERVICE_DOCTYPE);
			service.setPathInfo(api.getPathAsString(), api.getName());
			service.setProperty("dublincore", "title", title);
			session.createDocument(service);
			return service;
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
				DocumentModel appliImpl = createAppliImpl(session, APPLIIMPL_DOCTYPE);
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
		return findFirstDocument(session, APPLIIMPL_DOCTYPE, "app:rootServicesUrl", appliUrl);
	}
	
	public static DocumentModel findServiceApi(CoreSession session, String apiUrl) throws ClientException {
		return findFirstDocument(session, SERVICEAPI_DOCTYPE, "api:url", apiUrl);
	}
	
	public static DocumentModel findService(CoreSession session, String serviceUrl) throws ClientException {
		return findFirstDocument(session, SERVICE_DOCTYPE, "serv:url", serviceUrl);
	}

	private static DocumentModel findFirstDocument(CoreSession session, String type, String field, String value) throws ClientException {
		DocumentModelList apis = session.query("SELECT * FROM Document WHERE ecm:primaryType = '" + 
				type + "' AND " + field + " = '" +  value + "' AND ecm:currentLifeCycleState <> 'deleted'");
		return (apis != null && apis.size() > 0) ? apis.get(0) : null;
	}

	private static DocumentModel getWSRoot(CoreSession session) throws ClientException {
		if (wsRoot == null || !session.exists(wsRoot.getRef())) {
			DocumentModel root = DocumentService.getChild(session, session.getRootDocument().getRef(), DOMAIN_TITLE);
			if (root == null)
				return null;
			wsRoot = DocumentService.getChild(session, root.getRef(), WORKSPACE_ROOT_TITLE);
		}
		return wsRoot;
	}

	private static DocumentModel getChild(CoreSession session, DocumentRef parent, String childTitle) throws ClientException { 
		for (DocumentModel model : session.getChildren(parent)) {
			if (model.getTitle().equals(childTitle)) {
				return model;
			}
		}
		return null;
	}


}