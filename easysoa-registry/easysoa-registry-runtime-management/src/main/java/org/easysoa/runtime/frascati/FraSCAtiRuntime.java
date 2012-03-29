package org.easysoa.runtime.frascati;

import org.easysoa.runtime.api.RuntimeServer;
import org.easysoa.runtime.api.RuntimeControlService;
import org.easysoa.runtime.api.RuntimeDeployableService;
import org.easysoa.runtime.api.RuntimeEventService;

public class FraSCAtiRuntime implements RuntimeServer<FraSCAtiDeployable, RuntimeEventService> {

	private FraSCAtiControlService controlService;
	
	public FraSCAtiRuntime() {
		this.controlService = new FraSCAtiControlService();
	}

	@Override
	public RuntimeControlService getControlService() {
		return controlService;
	}

	@Override
	public RuntimeDeployableService<FraSCAtiDeployable> getDeployableService() {
		return null;
	}

	@Override
	public RuntimeEventService getEventService() {
		return null;
	}
	
}
