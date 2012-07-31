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

package org.easysoa.sca.intents.utils;

/**
 * 
 * 
 * @author jguillemotte
 *
 */
public class RequestElement {

	private long requestTime;
	private String request; 
	
	public RequestElement(String request, long requestTime) throws IllegalArgumentException {
		if(request != null){
			this.request = request;
		} else {
			throw new IllegalArgumentException("request parameter must not be null");
		}
		if(requestTime > 0){
			this.requestTime = requestTime;
		} else {
			throw new IllegalArgumentException("requestTime parameter must be set with a value > 0");
		}
	}
	
	public String getRequest(){
		return this.request;
	}
	
	public long getRequestTime(){
		return this.requestTime;
	}
	
}
