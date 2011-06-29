package org.easysoa;

import org.nuxeo.ecm.core.api.repository.RepositoryManager;
import org.nuxeo.runtime.api.Framework;
import org.nuxeo.runtime.model.ComponentContext;
import org.nuxeo.runtime.model.DefaultComponent;

/**
 * Component loaded on Nuxeo startup
 * @author mkalam-alami
 *
 */
public class EasySOAInitComponent extends DefaultComponent {

	public void activate(ComponentContext context) throws Exception {
		
		// Init domain
		new DomainInit(((RepositoryManager) Framework
				.getService(RepositoryManager.class)).getDefaultRepository()
				.getName()).runUnrestricted();
		
	}

}