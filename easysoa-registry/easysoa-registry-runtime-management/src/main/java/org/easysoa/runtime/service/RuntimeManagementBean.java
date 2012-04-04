package org.easysoa.runtime.service;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Create;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Install;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.platform.ui.web.api.NavigationContext;

/**
 * 
 * @author mkalam-alami
 * 
 */
@Name("runtimeManagement")
@Scope(ScopeType.CONVERSATION)
@Install(precedence = Install.FRAMEWORK)
public class RuntimeManagementBean {

	private static Log log = LogFactory.getLog(RuntimeManagementBean.class);

	@In(create = true, required = false)
	CoreSession documentManager;

	@In(create = true)
	NavigationContext navigationContext;

	@Create
	public void init() throws Exception {
		if (documentManager == null) {
			documentManager = navigationContext.getOrCreateDocumentManager();
		}
	}

	public void deploy() throws ClientException {
		log.info("Deploy: " + getCurrentDocTitle() + " (Not implemented yet)"); // TODO
	}

	public void start() throws ClientException {
		log.info("Start: " + getCurrentDocTitle() + " (Not implemented yet)"); // TODO
	}

	public void stop() throws ClientException {
		log.info("Stop: " + getCurrentDocTitle() + " (Not implemented yet)"); // TODO
	}

	public String getState() throws ClientException {
		return "Stopped"; // TODO
	}
	
	private String getCurrentDocTitle() throws ClientException {
		return navigationContext.getCurrentDocument().getTitle();
	}
}
