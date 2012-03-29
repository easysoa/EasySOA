package org.easysoa.runtime.api;

import java.security.InvalidParameterException;


public class DeployableController {

	private RuntimeServer<?, ?> runtime;
	
	public DeployableController(RuntimeServer<?, ?> runtime) {
		if (runtime == null) {
			throw new InvalidParameterException("A valid runtime must be specified");
		}
		setRuntime(runtime);
	}
	
	public void setRuntime(RuntimeServer<?, ?> runtime) {
		this.runtime = runtime;
	}

	public void addDeployableProvider(DeployableProvider<?, ?> deployableProvider) {
		// TODO Auto-generated method stub
		
	}

	public void deploy(Object id) {
		// TODO Auto-generated method stub
		
	}
	
	public void deploy(DeployableDescriptor<?> deployableDescriptor) {
		// TODO Auto-generated method stub
		
	}

	public void deployWithDependencies(Object id) {
		// TODO Auto-generated method stub
		
	}

	public void deployWithDependencies(DeployableDescriptor<?> deployableDescriptor) {
		// TODO Auto-generated method stub
		
	}
	
	public void startServer() {
		// TODO Auto-generated method stub
		
	}

}
