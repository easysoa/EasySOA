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

package com.openwide.easysoa.message;


/**
 * Represent an HTTP Header
 * @author jguillemotte
 * 
 */
public class Header {

	// Header name
	private String name;
	// Header value
	private String value;
	// comment
	private String comment;

	public Header(){
		this.name = "";
		this.value = "";
		this.comment = "";
	}
	
	/**
	 * Creates a new <code>Header</code> object with empty comment
	 * @param name Header name
	 * @param value Header value
	 */
	public Header(String name, String value) {
		this(name, value, "");
	}

	/**
	 * Creates a new <code>Header</code> object
	 * @param name Header name
	 * @param value Header value
	 * @param comment Comment
	 */
	public Header(String name, String value, String comment) {
		this.name = name;
		this.value = value;
		this.comment = comment;
	}

	/**
	 * Returns the header name
	 * @return The header name
	 */
	public String getName() {
		return name;
	}

	/**
	 * Returns the header value
	 * @return The Header Value
	 */
	public String getValue() {
		return value;
	}

	/**
	 * Returns the comment
	 * @return The comment
	 */
	public String getComment() {
		return comment;
	}

	/**
	 * 
	 * @param name
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * 
	 * @param value
	 */
	public void setValue(String value) {
		if("\"\"".equals(value)){
		    this.value = "";
		} else {
		    this.value = value;
		}
	}

	/**
	 * 
	 * @param comment
	 */
	public void setComment(String comment) {
		this.comment = comment;
	}

	
}
