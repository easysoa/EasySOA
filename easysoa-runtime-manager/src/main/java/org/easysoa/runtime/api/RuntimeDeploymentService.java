package org.easysoa.runtime.api;

import java.io.IOException;

/**
 * Allows to send or remove deployables.
 * 
 * @author mkalam-alami
 *
 */
public interface RuntimeDeploymentService {

	boolean deploy(Deployable<?> deployable) throws IOException;

	boolean undeploy(Deployable<?> deployable) throws IOException;

}
