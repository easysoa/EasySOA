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

package org.easysoa.registry.frascati;


public class FraSCAtiApp implements EasySOAApp {
	
	private final static String STARTING_METHOD = "NxFraSCAti";
	
	private String appPath;

	private FraSCAtiRegistryServiceBase fraSCAtiService; // TODO make it independent from nuxeo by reimplementing it also directly on top of FraSCAti
	
	
	@Override
	public void start() {
		// TODO start with disco
	}

	@Override
	public void stop() {
		// TODO stop compos
	}

	// init
	public void setAppPath(String appPath) {
		this.appPath = appPath;
	}

	public String getAppPath() {
		return appPath;
	}

	public FraSCAtiRegistryServiceBase getFraSCAtiService() {
		return fraSCAtiService;
	}

	// init
	public void setFraSCAtiService(FraSCAtiRegistryServiceBase fraSCAtiService) {
		this.fraSCAtiService = fraSCAtiService;
	}

	@Override
	public String getAppId() {
		return STARTING_METHOD + ":" + appPath; 
	}
	
}
