package org.easysoa.runtime.api;

import java.io.InputStream;

public interface RuntimeServer<T extends RuntimeEventService> {

	InputStream getAsInputStream();

	RuntimeControlService getControlService();

	RuntimeDeploymentService getDeploymentService();

	T getEventService();

}
