package org.easysoa.runtime.copypaste;

import java.util.ArrayList;
import java.util.List;

import org.easysoa.runtime.api.Deployable;
import org.easysoa.runtime.api.RuntimeEventService;
import org.easysoa.runtime.api.event.OnDeployListener;
import org.easysoa.runtime.api.event.OnUndeployListener;

public class CopyPasteServerEventService implements RuntimeEventService {
	
	private List<OnDeployListener> onDeployListeners = new ArrayList<OnDeployListener>();
	
	private List<OnUndeployListener> onUndeployListeners = new ArrayList<OnUndeployListener>();
	
	public void addOnDeployListener(OnDeployListener listener) {
		onDeployListeners.add(listener);
	}

	public void addOnUndeployListener(OnUndeployListener listener) {
		onUndeployListeners.add(listener);
	}

	protected void onDeploy(Deployable<?> deployable) {
		for (OnDeployListener listener : onDeployListeners) {
			listener.onDeploy(deployable);
		}
	}

	protected void onUndeploy(Deployable<?> deployable) {
		for (OnUndeployListener listener : onUndeployListeners) {
			listener.onUndeploy(deployable);
		}
	}
	
}
