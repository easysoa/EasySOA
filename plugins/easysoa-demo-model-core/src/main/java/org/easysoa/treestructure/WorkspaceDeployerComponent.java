package org.easysoa.treestructure;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nuxeo.ecm.core.api.repository.RepositoryManager;
import org.nuxeo.runtime.api.Framework;
import org.nuxeo.runtime.model.ComponentContext;
import org.nuxeo.runtime.model.DefaultComponent;

/**
 * Component loaded on Nuxeo startup, used to deploy the workspace
 * @author mkalam-alami
 *
 */
public class WorkspaceDeployerComponent extends DefaultComponent {
	
	private static final Log log = LogFactory.getLog(WorkspaceDeployerComponent.class);
	private WorkspaceDeployer wsd = null;

	public void activate(ComponentContext context) throws Exception {
		log.debug("Default repository name: "
				+ ((RepositoryManager) Framework
						.getService(RepositoryManager.class))
						.getDefaultRepository().getName());

		this.wsd = new WorkspaceDeployer(((RepositoryManager) Framework
				.getService(RepositoryManager.class)).getDefaultRepository()
				.getName());

		this.wsd.runUnrestricted();
	}

	public List<String> getDescriptorTypes() {
		return this.wsd.getDescriptorTypes();
	}
}