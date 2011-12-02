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

import javax.xml.bind.annotation.XmlRootElement;

/**
 * Post data
 * @author jguillemotte
 *
 */
public class PostData {

	private String mimeType;
	//private PostDataParams params;
	private String data;
	private String comment;
	private CustomFields customFields = new CustomFields();

	public PostData(){}
	
	/**
	 * Creates a new <code>HarPostData</code> object
	 * @param mimeType Mime type of posted data.
	 * @param data - Plain text posted data
	 * @param comment user comment
	 */
	public PostData(String mimeType, String data, String comment) {
		this.mimeType = mimeType;
		this.data = data;
		this.comment = comment;
	}

	/**
	 * Returns the mimeType.
	 * @return Returns the mimeType.
	 */
	public String getMimeType() {
		return mimeType;
	}

	/**
	 * Sets the mimeType.
	 * @param mimeType The mimeType to set.
	 */
	public void setMimeType(String mimeType) {
		this.mimeType = mimeType;
	}

	/**
	 * Returns the data.
	 * @return Returns the data.
	 */
	public String getData() {
		return data;
	}

	/**
	 * Sets the data.
	 * @param data The data to set.
	 */
	public void setData(String data) {
		this.data = data;
	}

	/**
	 * Returns the comment.
	 * @return Returns the comment.
	 */
	public String getComment() {
		return comment;
	}

	/**
	 * Sets the comment.
	 * @param comment The comment to set.
	 */
	public void setComment(String comment) {
		this.comment = comment;
	}

	/**
	 * Returns the customFields.
	 * @return Returns the customFields.
	 */
	public CustomFields getCustomFields() {
		return customFields;
	}

	/**
	 * Sets the customFields.
	 * @param customFields The customFields to set.
	 */
	public void setCustomFields(CustomFields customFields) {
		this.customFields = customFields;
	}

}
