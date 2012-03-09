package org.easysoa.runtime.api.event;

import java.util.EventListener;

import org.easysoa.runtime.api.Deployable;

public interface OnUndeployListener extends EventListener {
	
	public void onUndeploy(Deployable<?> deployable);

}
