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
 * Message content
 * @author jguillemotte
 *
 */
public class MessageContent {

	private Long size;
	private String mimeType;
	private String content;
	private String encoding;
	private String comment;
	//private CustomFields customFields = new CustomFields();

	public MessageContent(){
		this.size = 0L;
		this.comment = "";
		this.content = "";
		this.mimeType = "";
		this.encoding = "";
	}
	
	/**
	 * Creates a new <code>MessageContent</code> object
	 * @param size BodySize
	 * @param mimeType MimeType
	 * @param text Response body
	 * @param encoding Encoding
	 * @param comment Optional user comment.
	 */
	public MessageContent(long size, String mimeType, String content, String encoding, String comment) {
		this.size = size;
		this.mimeType = mimeType;
		this.content = content;
		this.encoding = encoding;
		this.comment = comment;
	}

	/**
	 * Creates a new <code>MessageContent</code> object
	 * with empty text, encoding and comment
	 */
	public MessageContent(long size, String mimeType) {
		this(size, mimeType, "", "", "");
	}

	/**
	 * Returns the content size.
	 * @return Returns the content size.
	 */
	public long getSize() {
		return size;
	}

	/**
	 * Sets the size.
	 * @param size The size to set.
	 */
	public void setSize(long size) {
		this.size = size;
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
	 * Returns the text.
	 * @return Returns the text
	 */
	public String getContent() {
		return content;
	}

	/**
	 * Sets the text.
	 * @param text The text to set.
	 */
	public void setContent(String content) {
		content.replace("\"", "\\\"");
	    this.content = content;
	}

	/**
	 * Returns the encoding.
	 * @return Returns the encoding.
	 */
	public String getEncoding() {
		return encoding;
	}

	/**
	 * Sets the encoding.
	 * @param encoding The encoding to set.
	 */
	public void setEncoding(String encoding) {
		this.encoding = encoding;
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
	 * Returns the customFields value.
	 * @return Returns the customFields.
	 */
	/*public CustomFields getCustomFields() {
		return customFields;
	}*/

	/**
	 * Sets the customFields.
	 * @param customFields The customFields to set.
	 */
	/*public void setCustomFields(CustomFields customFields) {
		this.customFields = customFields;
	}*/

}
