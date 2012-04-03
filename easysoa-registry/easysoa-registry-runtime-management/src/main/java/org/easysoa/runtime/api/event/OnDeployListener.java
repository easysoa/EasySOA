package org.easysoa.runtime.api.event;

import java.util.EventListener;

import org.easysoa.runtime.api.Deployable;

public interface OnDeployListener extends EventListener {
	
	public void onDeploy(Deployable<?> deployable);

}
