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

package org.easysoa.runtime.frascati;

import org.easysoa.runtime.api.RuntimeServer;
import org.easysoa.runtime.api.RuntimeControlService;
import org.easysoa.runtime.api.RuntimeDeployableService;
import org.easysoa.runtime.api.RuntimeEventService;

public class FraSCAtiRuntime implements RuntimeServer<FraSCAtiDeployable, RuntimeEventService> {

    private FraSCAtiControlService controlService;
	
    /**
     * 
     */
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

	@Override
	public String getName() {
		return "FraSCAti";
	}

}
