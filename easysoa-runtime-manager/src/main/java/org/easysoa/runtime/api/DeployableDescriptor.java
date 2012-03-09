package org.easysoa.runtime.api;

import java.util.List;

/**
 * Provides information about a deployable artifact.
 * 
 * @author mkalam-alami
 *
 */
public interface DeployableDescriptor<T> {

	/**
	 * @return Any ID or hash defining the deployable in a unique way
	 */
	T getId();
	
	/**
	 * @return Some or all of the needed deployables in order for this one to run.
	 * Use <code>isAllDependenciesKnown</code> to check the completeness of this list.
	 */
	List<DeployableDescriptor<?>> getDependencies();
	
	/**
	 * Allows to check if it is sure that no dependency is missing when using <code>getDependencies</code>.
	 * Might be false if the Descriptor has been produced from some incomplete information
	 * (for instance extrapolated from the EasySOA model).
	 * @return
	 */
	boolean areAllDependenciesKnown();
	
}
