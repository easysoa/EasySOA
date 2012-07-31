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

package org.easysoa.message;

import java.util.ArrayList;
import java.util.List;

/**
 * Collection of query parameters
 * @author jguillemotte
 *
 */
public class QueryString {

	private List<QueryParam> queryParams;

	/**
	 * Creates a new <code>QueryString</code> object
	 */
	public QueryString() {
		queryParams = new ArrayList<QueryParam>();
	}

	/**
	 * Add a new query parameter to the list
	 * @param queryParam The <code>QueryParam</code> to add
	 */
	public void addQueryParam(QueryParam queryParam) {
		queryParams.add(queryParam);
	}

	/**
	 * Returns the query parameters.
	 * @return Returns the query parameters.
	 */
	public List<QueryParam> getQueryParams() {
		return queryParams;
	}

	/**
	 * Sets the query parameters.
	 * @param queryParams The queryParams to set.
	 */
	public void setQueryParams(List<QueryParam> queryParams) {
		this.queryParams = queryParams;
	}
	
	/**
	 * Builds a query param string from the query param list (eg : param1=value1&param2=value2&param3=value3 ...)
	 * @return A query param <code>String</code>
	 */
	public String makeString(){
		StringBuffer buffer = new StringBuffer();
		for(QueryParam param : this.queryParams){
			if(buffer.length() > 0){
				buffer.append("&");
			}
			buffer.append(param.getName());
			buffer.append("=");
			buffer.append(param.getValue());
		}
		return buffer.toString();
	}

}
