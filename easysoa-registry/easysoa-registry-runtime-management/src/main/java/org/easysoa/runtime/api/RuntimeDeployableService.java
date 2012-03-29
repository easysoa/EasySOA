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

	boolean deploy(T deployable) throws IOException;

	boolean undeploy(T deployable) throws IOException;

	boolean start(T deployable) throws UnsupportedOperationException;

	boolean stop(T deployable) throws UnsupportedOperationException;
	
	List<T> getDeployedDeployables();
	
}
