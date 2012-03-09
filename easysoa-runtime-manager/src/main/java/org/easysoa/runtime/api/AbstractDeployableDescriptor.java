package org.easysoa.runtime.api;

import java.util.ArrayList;
import java.util.List;

/**
 * Convenience class to build {@link DeployableDescriptor} classes.
 * Handles basic dependencies storage and implements its API.
 * 
 * Should handle most needs, with the following limitations:
 * - There is only support for one deployable ID interface ;
 * - Setters are made public.
 * 
 * @author mkalam-alami
 *
 * @param <T> The deployable ID class
 */
public abstract class AbstractDeployableDescriptor<T> implements DeployableDescriptor<T> {

	protected List<DeployableDescriptor<?>> dependencies;
	
	protected boolean allDependenciesKnown;
	
	protected T id;
	
	public AbstractDeployableDescriptor(T id) {
		this(id, null, false);
	}
	
	public AbstractDeployableDescriptor(T id, List<DeployableDescriptor<?>> dependencies) {
		this(id, dependencies, false);
	}
	
	public AbstractDeployableDescriptor(T id, List<DeployableDescriptor<?>> dependencies, boolean allDependenciesKnown) {
		this.id = id;
		this.dependencies = dependencies;
		this.allDependenciesKnown = allDependenciesKnown;
		if (this.dependencies == null) {
			this.dependencies = new ArrayList<DeployableDescriptor<?>>();
		}
	}
	
	@Override
	public T getId() {
		return id;
	}
	
	public List<DeployableDescriptor<?>> getDependencies() {
		return dependencies;
	}

	public void addDependency(DeployableDescriptor<?> dependency) {
		this.dependencies.add(dependency);
	}

	public boolean areAllDependenciesKnown() {
		return allDependenciesKnown;
	}

	public void setAllDependenciesKnown(boolean value) {
		this.allDependenciesKnown = true;
	}

	
}
