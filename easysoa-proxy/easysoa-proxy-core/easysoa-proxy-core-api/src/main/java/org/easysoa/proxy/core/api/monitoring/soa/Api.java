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

package org.easysoa.proxy.core.api.monitoring.soa;

public class Api extends Node {

    private String application;
    private String parentUrl;
    private String sourceUrl;
    /*
	"title": "The name of the document.",
    "application": "The related business application.",
    "description": "A short description.",
    "parentUrl": "(mandatory) The parent URL, which is either another service API, or the service root.",
    "sourceUrl": "The web page where the service has been found (useful for REST only).",
    "url": "(mandatory) Service API url (WSDL address, parent path...)."
    */
    
    public Api(String url, String parentUrl) throws IllegalArgumentException {
    	super(url);
		if(parentUrl == null || "".equals(parentUrl)){
			throw new IllegalArgumentException("parentUrl must not be null or empty");
		}
    	this.parentUrl = parentUrl;
    	this.application = "";
    	this.sourceUrl = "";
    }
    
	public String getApplication() {
		return application;
	}
	public void setApplication(String application) {
		this.application = application;
	}
	public String getSourceUrl() {
		return sourceUrl;
	}
	public void setSourceUrl(String sourceUrl) {
		this.sourceUrl = sourceUrl;
	}
	public String getParentUrl() {
		return parentUrl;
	}
}
