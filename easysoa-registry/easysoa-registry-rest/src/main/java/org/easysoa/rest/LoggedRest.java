package org.easysoa.rest;

import javax.security.auth.login.LoginContext;
import javax.security.auth.login.LoginException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.webengine.WebEngine;
import org.nuxeo.runtime.api.Framework;

/**
 * Extend this class to allow your REST service to log as an administrator.
 * (XXX: obviously not a secured solution for a production environment)
 * @author mkalam-alami
 *
 */
public abstract class LoggedRest {

	private static final Log log = LogFactory.getLog(LoggedRest.class);

	protected LoginContext loginContext;
	protected CoreSession session = null;
	
	protected void login() throws LoginException {
		// XXX: As the REST API is (for now) anonymously available, we need to explicitly log in
		loginContext = Framework.login("Administrator", "Administrator");
		if (loginContext == null) { // XXX: Probably useless (added for unit testing)
			loginContext = Framework.login("sa", "");
		}
		session = WebEngine.getActiveContext().getUserSession().getCoreSession(null);
        if (session == null) {
            throw new LoginException("Failed to login");
        }
	}
	
	protected void logout() {
		try {
		    if (loginContext != null) {
		        loginContext.logout();
		    }
		} catch (LoginException e) {
			log.warn("Failed to logout");
		}
	}
	
}