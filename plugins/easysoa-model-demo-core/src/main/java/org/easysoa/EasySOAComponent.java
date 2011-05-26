package org.easysoa;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.DocumentRef;
import org.nuxeo.ecm.core.api.repository.RepositoryManager;
import org.nuxeo.runtime.api.Framework;
import org.nuxeo.runtime.model.ComponentContext;
import org.nuxeo.runtime.model.DefaultComponent;

/**
 * Component loaded on Nuxeo startup, used to deploy the workspace
 * @author mkalam-alami
 *
 */
public class EasySOAComponent extends DefaultComponent {
	
	@SuppressWarnings("unused")
	private static final Log log = LogFactory.getLog(EasySOAComponent.class);
	private EasySOA wsd = null;

	public void activate(ComponentContext context) throws Exception {
		this.wsd = new EasySOA(((RepositoryManager) Framework
				.getService(RepositoryManager.class)).getDefaultRepository()
				.getName());
		this.wsd.runUnrestricted();
	}

}