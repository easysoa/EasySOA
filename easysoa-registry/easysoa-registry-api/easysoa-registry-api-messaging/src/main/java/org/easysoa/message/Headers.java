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
 * Header collection
 * @author jguillemotte
 *
 */
public class Headers {

	private List<Header> headerList;

	/**
	 * Creates a new <code>Headers</code> object
	 */
	public Headers() {
		headerList = new ArrayList<Header>();
	}

	/**
	 * Add a new header to the list
	 * @param header The header to add
	 */
	public void addHeader(Header header) {
		headerList.add(header);
	}

    /**
     * Return the value of the header
     * @param headerName The Header name (not case sensitive)
     * @return The header value, null if the header name is not found
     */
    public String getHeaderValue(String headerName){
        for(Header header : headerList){
            if(header.getName().equalsIgnoreCase(headerName)){
                return header.getValue();
            }
        }
        return null;
    }

    /**
     * Returns a list of header names
     * @return A list containing the header names
     */
    public List<String> getHeaderNames(){
        List<String> headerNameList = new ArrayList<String>();
        for(Header header : headerList){
            headerNameList.add(header.getName());
        }
        return headerNameList;
    }

	/**
	 * Returns the headers.
	 * @return Returns the headers.
	 */
	public List<Header> getHeaderList() {
		return headerList;
	}

	/**
	 * Sets the headers value.
	 * @param headers The headers to set.
	 */
	public void setHeaderList(List<Header> headers) {
		this.headerList = headers;
	}

}
