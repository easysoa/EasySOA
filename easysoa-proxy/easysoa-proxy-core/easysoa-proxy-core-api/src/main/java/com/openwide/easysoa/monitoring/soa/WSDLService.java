/**
 * EasySOA Proxy
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

package com.openwide.easysoa.monitoring.soa;

public class WSDLService {

	private String appName;
	private String serviceName;
	private String url;
	private String type;
	
	/**
	 * Constructor
	 * @param appName Application name
	 * @param serviceName Service name
	 * @param url Service URL
	 */
	public WSDLService(String appName, String serviceName, String url, String type){
		this.appName = appName;
		this.serviceName = serviceName;
		this.url = url;
		this.type = type;
	}

	/**
	 * Application name
	 * @return Application name
	 */
	public String getAppName() {
		return appName;
	}

	/**
	 * Service name
	 * @return Service name
	 */
	public String getServiceName(){
		return serviceName;
	}
	
	/**
	 * Service URL
	 * @return Service URL
	 */
	public String getUrl() {
		return url;
	}
	
	/**
	 * Service type : WSDl, REST ...
	 * @return
	 */
	public String getType(){
		return type;
	}
}
