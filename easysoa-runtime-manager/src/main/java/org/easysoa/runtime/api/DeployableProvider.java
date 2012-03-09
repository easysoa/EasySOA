package org.easysoa.runtime.api;

/**
 * Mean to retrieve deployables.
 * 
 * @author mkalam-alami
 *
 */
public interface DeployableProvider<T> {
	
	/**
	 * Retrieves the specified deployable 
	 * @param descriptor
	 * @return
	 */
	Deployable<T> getDeployable(DeployableDescriptor<T> descriptor);

}
