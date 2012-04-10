/**
 * EasySOA Registry
 * Copyright 2011 Open Wide
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * 
 * Contact : easysoa-dev@googlegroups.com
 */

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
	
	public RuntimeServer<?, ?> getRuntime() {
		return runtime;
	}

	public void addDeployableProvider(DeployableProvider<?> deployableProvider) {
		deployableProviders.add(deployableProvider);
	}

	/**
	 * Deploys the deployable of given ID
	 * @param id
	 * @return The deployed deployable, or null if none was found or the deployment failed
	 * @throws IOException
	 */
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

	public boolean stopServer() {
		if (this.controlSupported) {
			return runtime.getControlService().stop();
		}
		else {
			return false;
		}
	}

}
