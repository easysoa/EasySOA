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


/**
 * Represents an EasySOA application.
 * 
 * Still TODO on this API, in FraSCAti implementation(s) (our own, Studio...) and others (Light, Talend ?) :
 * state (in FraSCAti only STARTED & STOPPED, add STARTING & STOPPING)
 * an evented way to be updated by info especially state change, ex. nuxeo events (but only nuxeo) or handlers (beware of FraSCAti wrapped side)
 * provisioning : how the app is provided at the place said (path) ; for now manually as artifactItems, LATER do it at registerContribution time, LATER for other impls
 * packaging : how the app is packaged ; for now jar(s) & root composite name
 * management UI : for now web explorer TODO test it ; LATER list, integrated in environment UI & env start...
 * 
 * @author jguillemotte
 *
 */
public interface EasySOAApp {
	
	/**
	 * Unique app id, built using way of starting it and root app resource (ex. "NxFraSCAti:target/httpDiscovery.composite") 
	 * LATER link app with registry model (using runtime discovery) and with desc info stored there
	 * LATER put in model more info like this (appId) about how it is started up
	 * @return
	 */
	public String getAppId();

	/**
	 * Start the EasySOA app
	 */
	public void start();

	/**
	 * Stop the EasySOA app
	 */	
	public void stop();
	
	//public AppState getState();
	
	//public FraSCAtiCompositeItf[] getComposite();
	
}
