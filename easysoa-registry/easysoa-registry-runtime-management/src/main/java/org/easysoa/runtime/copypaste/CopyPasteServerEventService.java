/**
 * EasySOA Registry
 * Copyright 2011 Open Wide
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * 
 * Contact : easysoa-dev@googlegroups.com
 */

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
