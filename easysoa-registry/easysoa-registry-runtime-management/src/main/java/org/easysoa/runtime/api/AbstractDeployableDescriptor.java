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
