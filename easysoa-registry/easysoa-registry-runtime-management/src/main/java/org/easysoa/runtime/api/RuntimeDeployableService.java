package org.easysoa.runtime.api;

import java.io.IOException;
import java.util.List;

/**
 * Allows to send or remove deployables.
 * 
 * @author mkalam-alami
 *
 */
public interface RuntimeDeployableService<T extends Deployable<?>> {

	boolean deploy(Deployable<?> deployable) throws IOException;

	boolean undeploy(Deployable<?> deployable) throws IOException;

	boolean start(Deployable<?> deployable) throws UnsupportedOperationException;

	boolean stop(Deployable<?> deployable) throws UnsupportedOperationException;
	
	List<T> getDeployedDeployables();
	
}
