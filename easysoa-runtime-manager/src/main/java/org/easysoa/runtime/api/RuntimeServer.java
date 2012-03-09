package org.easysoa.runtime.api;

/**
 * Allows to interact with a runtime server.
 * Features are made available through various services that can be implemented or not.
 * 
 * @author mkalam-alami
 *
 * @param <T> The server's event service interface
 */
public interface RuntimeServer<T extends RuntimeEventService> {

	RuntimeControlService getControlService();

	RuntimeDeploymentService getDeploymentService();

	T getEventService();

}
