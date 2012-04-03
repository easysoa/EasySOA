package org.easysoa.runtime.api;

import java.io.IOException;

/**
 * Mean to retrieve deployables.
 * 
 * @author mkalam-alami
 *
 */
public interface DeployableProvider<T extends Deployable<?>> {
	
	/**
	 * Retrieves the specified deployable 
	 * @param descriptor
	 * @return
	 */
	T fetchDeployable(Object id) throws IOException;

}
