package org.easysoa.runtime.api;

import java.io.IOException;


/**
 * Source of information regarding deployables.
 * 
 * @author mkalam-alami
 *
 */
public interface DeployableDescriptorProvider<T extends DeployableDescriptor<U>, U> {

	/**
	 * The deployable descriptor matching the requested ID.
	 * @param id The deployable ID
	 * @return The matching descriptor or null
	 */
	T fetchDeployableDescriptor(U id) throws IOException;

}
