package org.easysoa;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nuxeo.ecm.core.api.repository.Repository;
import org.nuxeo.ecm.core.api.repository.RepositoryManager;
import org.nuxeo.runtime.api.Framework;
import org.nuxeo.runtime.model.ComponentContext;
import org.nuxeo.runtime.model.DefaultComponent;

/**
 * Component loaded on Nuxeo startup
 * 
 * @author mkalam-alami
 *
 */
public class EasySOAInitComponent extends DefaultComponent {

	private static Log log = LogFactory.getLog(EasySOAInitComponent.class);
	
	public void activate(ComponentContext context) throws Exception {
		
		RepositoryManager repoService = Framework.getService(RepositoryManager.class);

		// Init default domain
	    Repository defaultRepository = repoService.getDefaultRepository();
	    if (defaultRepository != null) {
	        new DomainInit(defaultRepository.getName()).runUnrestricted();
	    }
	    else {
	        log.warn("Failed to access default repository for initialization: no default repository");
	    }
		
	}

}