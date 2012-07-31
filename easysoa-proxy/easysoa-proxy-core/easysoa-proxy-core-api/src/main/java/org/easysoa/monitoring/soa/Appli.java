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

package org.easysoa.monitoring.soa;

public class Appli extends Node {

	/*
	 * { "description":
	 * "Notification concerning an application implementation.", "parameters": {
	 * "rootServicesUrl": "(mandatory) Services root.", "uiUrl":
	 * "Application GUI entry point.", "title": "The name of the document.",
	 * "technology": "Services implementation technology.", "standard":
	 * "Protocol standard if applicable.", "sourcesUrl": "Source code access.",
	 * "description": "A short description.", "server": "IP of the server." } }
	 */

	/**
	 * Application GUI entry point.
	 */
	private String uiUrl;
	
	/**
	 * Services implementation technology
	 */
	private String technology;
	
	/**
	 * Protocol standard if applicable
	 */
	private String standard;
	
	/**
	 * Source code access
	 */
	private String sourcesUrl;
	
	/**
	 * IP of the server.
	 */
	private String server;

	/**
	 * 
	 * @param rootServicesUrl
	 * @throws IllegalArgumentException
	 */
	public Appli(String appliName, String url) throws IllegalArgumentException {
		super(url);
		if(appliName == null || "".equals(appliName)){
			throw new IllegalArgumentException("appliname must not be null or empty");
		}
		this.server = "";
		this.sourcesUrl = "";
		this.standard = "";
		this.technology = "";
		this.uiUrl = "";
	}
	
	public String getUiUrl() {
		return uiUrl;
	}

	public void setUiUrl(String uiUrl) {
		this.uiUrl = uiUrl;
	}

	public String getTechnology() {
		return technology;
	}

	public void setTechnology(String technology) {
		this.technology = technology;
	}

	public String getStandard() {
		return standard;
	}

	public void setStandard(String standard) {
		this.standard = standard;
	}

	public String getSourcesUrl() {
		return sourcesUrl;
	}

	public void setSourcesUrl(String sourcesUrl) {
		this.sourcesUrl = sourcesUrl;
	}

	public String getServer() {
		return server;
	}

	public void setServer(String server) {
		this.server = server;
	}
}
