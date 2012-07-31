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

package org.easysoa.proxy;

/**
 * Proxy interface. SLA Messaging interface.
 * @author jguillemotte
 *
 */
public interface Proxy {
		
	/**
	 * preForward (ex. SLA)
	 * 
	 */
	public void preForward();
	//postForward (ex. async)
	public void postForward();
	//preReturn (ex. translate)
	public void preReturn();
	//postReturn (ex. record log including return OK)
	public void postReturn();

	//handle errors and aborts (ex. SLA).
	public void handle(); 
	
}
