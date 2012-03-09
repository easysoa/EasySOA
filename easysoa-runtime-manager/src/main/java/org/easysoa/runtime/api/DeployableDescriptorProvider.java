package org.easysoa.runtime.api;

import java.util.List;

/**
 * Source of information regarding deployables.
 * 
 * @author mkalam-alami
 *
 */
public interface DeployableDescriptorProvider<T extends DeployableDescriptor<?>> {
	
	/**
	 * @return A set of descriptors of all found deployables.
	 */
	List<T> getDeployableDescriptors();

}
