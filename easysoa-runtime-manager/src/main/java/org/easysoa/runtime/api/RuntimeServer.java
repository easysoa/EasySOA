package org.easysoa.runtime.api;


public interface RuntimeServer<T extends RuntimeEventService> {

	RuntimeControlService getControlService();

	RuntimeDeploymentService getDeploymentService();

	T getEventService();

}
