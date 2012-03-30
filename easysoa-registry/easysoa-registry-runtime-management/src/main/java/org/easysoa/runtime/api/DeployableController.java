package org.easysoa.runtime.api;

import java.io.IOException;
import java.security.InvalidParameterException;
import java.util.LinkedList;
import java.util.List;

public class DeployableController {

	private RuntimeServer<?, ?> runtime;
	private List<DeployableProvider<?>> deployableProviders = new LinkedList<DeployableProvider<?>>();
	
	private boolean deploySupported, controlSupported;
	
	public DeployableController(RuntimeServer<?, ?> runtime) {
		if (runtime == null) {
			throw new InvalidParameterException("A valid runtime must be specified");
		}
		setRuntime(runtime);
	}
	
	public void setRuntime(RuntimeServer<?, ?> runtime) {
		this.runtime = runtime;
		this.deploySupported = runtime.getDeployableService() != null;
		this.controlSupported = runtime.getControlService() != null;
	}

	public void addDeployableProvider(DeployableProvider<?> deployableProvider) {
		deployableProviders.add(deployableProvider);
	}

	public Deployable<?> deploy(Object id) throws IOException {
		Deployable<?> deployable = null;
		if (this.deploySupported) {
			// Find the deployable
			for (DeployableProvider<?> deployableProvider : deployableProviders) {
				deployable = deployableProvider.fetchDeployable(id);
				if (deployable != null) {
					break;
				}
			}
			// Deploy it
			if (deployable != null) {
				runtime.getDeployableService().deploy(deployable);
			}
		}
		return deployable;
	}
	
	public Deployable<?> deploy(DeployableDescriptor<?> deployableDescriptor) throws IOException {
		return deploy(deployableDescriptor.getId());
	}

	public Deployable<?> deployWithDependencies(DeployableDescriptor<?> deployableDescriptor) throws IOException {
		Deployable<?> deployed = deploy(deployableDescriptor);
		if (deployed != null) {
			List<DeployableDescriptor<?>> dependencies = deployableDescriptor.getDependencies();
			for (DeployableDescriptor<?> dependency : dependencies) {
				deploy(dependency);
			}
		}
		return deployed;
	}
	
	public boolean startServer() {
		if (this.controlSupported) {
			return runtime.getControlService().start();
		}
		else {
			return false;
		}
	}

}
