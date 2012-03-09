package org.easysoa.runtime.api;

/**
 * Allows to send or remove deployables
 * 
 * @author mkalam-alami
 *
 */
public interface RuntimeDeploymentService {

	boolean deploy(Deployable<?> deployable);

	boolean remove(Deployable<?> deployable);

}
