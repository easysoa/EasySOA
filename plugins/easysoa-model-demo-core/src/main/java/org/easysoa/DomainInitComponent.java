package org.easysoa;

import org.nuxeo.ecm.core.api.repository.RepositoryManager;
import org.nuxeo.runtime.api.Framework;
import org.nuxeo.runtime.model.ComponentContext;
import org.nuxeo.runtime.model.DefaultComponent;

/**
 * Component loaded on Nuxeo startup, used to deploy the workspace
 * @author mkalam-alami
 *
 */
public class DomainInitComponent extends DefaultComponent {
	
	private DomainInit wsd = null;

	public void activate(ComponentContext context) throws Exception {
		this.wsd = new DomainInit(((RepositoryManager) Framework
				.getService(RepositoryManager.class)).getDefaultRepository()
				.getName());
		this.wsd.runUnrestricted();
	}

}