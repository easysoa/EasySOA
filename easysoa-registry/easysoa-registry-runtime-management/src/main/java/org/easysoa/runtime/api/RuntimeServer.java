package org.easysoa.runtime.api;

public interface RuntimeServer<T extends Deployable<?>, U extends RuntimeEventService> {
	
	public RuntimeControlService getControlService();

	public RuntimeDeployableService<T> getDeployableService();

	public U getEventService();

}
