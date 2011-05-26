package org.easysoa;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentRef;
import org.nuxeo.ecm.core.api.UnrestrictedSessionRunner;

/**
 * Creates all needed folders on Nuxeo startup,
 * and provides common constants.
 * 
 * @author mkalam-alami
 * 
 */
public class EasySOA extends UnrestrictedSessionRunner {
	
	@SuppressWarnings("unused")
	private static final Log log = LogFactory.getLog(EasySOA.class);
	
	public static final String DOMAIN_NAME = "EasySOA";
	public static final String WORKSPACE_ROOT_NAME = "Service Registry";
	public static final String DEFAULT_APPLIIMPL_NAME = "Default application";

	public static final String APPLIIMPL_DOCTYPE = "Workspace";
	public static final String SERVICEAPI_DOCTYPE = "ServiceAPI";
	public static final String SERVICE_DOCTYPE = "Service";
	
	private static DocumentRef defaultAppliImpl = null;

	public EasySOA(String repositoryName) {
		super(repositoryName);
	}

	/**
	 * Sets up default domain
	 */
	public void run() throws ClientException {
		
		DocumentModel root = getChild(session, this.session.getRootDocument().getRef(), "Default domain");
		if (root != null) {
			
			root.setProperty("dublincore", "title", DOMAIN_NAME);
			session.move(root.getRef(), root.getParentRef(), DOMAIN_NAME);
			session.saveDocument(root);
			
			for (DocumentModel rootChild : session.getChildren(root.getRef())) {
				if (!rootChild.getType().equals("WorkspaceRoot")) {
					session.removeDocument(root.getRef());
				}
				else {
					rootChild.setProperty("dublincore", "title", WORKSPACE_ROOT_NAME);
					session.move(rootChild.getRef(), rootChild.getParentRef(), WORKSPACE_ROOT_NAME);
					
					getDefaultAppliImpl(session);
				}
			}
			
			session.save();
			
		}
	}
	
	/**
	 * Returns the default Appli Impl., creates it if necessary.
	 * @param session
	 * @return
	 * @throws ClientException
	 */
	public static DocumentRef getDefaultAppliImpl(CoreSession session) throws ClientException {
		if (defaultAppliImpl == null || !session.exists(defaultAppliImpl)) {

			DocumentModel root = getChild(session, session.getRootDocument().getRef(), DOMAIN_NAME);
			if (root == null)
				return null;
			DocumentModel wsroot = getChild(session, root.getRef(), WORKSPACE_ROOT_NAME);
			if (wsroot == null)
				return null;
			DocumentModel appliimpl = getChild(session, wsroot.getRef(), DEFAULT_APPLIIMPL_NAME);
			if (appliimpl == null) {
				DocumentModel appliImpl = session.createDocumentModel(APPLIIMPL_DOCTYPE);
				appliImpl.setPathInfo(wsroot.getPathAsString(), DEFAULT_APPLIIMPL_NAME);
				session.createDocument(appliImpl);
				session.save();
				defaultAppliImpl = appliImpl.getRef();
				return defaultAppliImpl;
			}
			else
				defaultAppliImpl = appliimpl.getRef();
		}
		return defaultAppliImpl;
	}

	public static DocumentModel getChild(CoreSession session, DocumentRef parent, String childTitle) throws ClientException { 
		for (DocumentModel model : session.getChildren(parent)) {
			if (model.getTitle().equals(childTitle)) {
				return model;
			}
		}
		return null;
	}
	
}