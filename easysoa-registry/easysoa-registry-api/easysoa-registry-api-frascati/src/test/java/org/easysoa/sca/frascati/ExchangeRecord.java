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

package org.easysoa.sca.frascati;

import java.io.IOException;
import java.util.Scanner;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

public class ExchangeRecord {

	//private ServletRequest request;
	private String requestContent;
	private ServletResponse response;
	
	/**
	 * Build a new ExchangeRecord object 
	 * @param request The request
	 * @param response The response
	 * @throws IOException 
	 */
	public ExchangeRecord(ServletRequest request, ServletResponse response) throws IOException {
		this.requestContent = new Scanner(request.getInputStream()).useDelimiter("\\A").next();
		this.response = response;
	}
	
	/**
	 * returns the exchange request
	 * @return <code>ServletRequest</code>
	 */
	public String getRequestContent(){
		return this.requestContent;
	}
	
	/**
	 * Returns the exchange response
	 * @return <code>ServletResponse</code>
	 */
	public ServletResponse getResponse(){
		return this.response;
	}
	
}
