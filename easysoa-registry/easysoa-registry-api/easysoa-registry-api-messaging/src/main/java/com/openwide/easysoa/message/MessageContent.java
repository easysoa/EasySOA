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

import java.io.StringReader;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import net.sf.json.JSON;
import net.sf.json.JSONSerializer;
import com.openwide.easysoa.message.util.ContentChecker;
import com.openwide.easysoa.message.util.ContentChecker.ContentType;

/**
 * Message content
 * @author jguillemotte
 *
 */
public class MessageContent {

    // TODO : change the content attribute to rawContent and add a generic java parsed content.
    // need 2 different java methods for JSON and XML ....
    
	private Long size;
	private String mimeType;
	private String rawContent;
	private ContentType contentType;
    private String encoding;
	private String comment;
	
	// Contains an XML or JSON JAVA structure of the rawContent 
	private JSON JSONContent;
	private XMLEventReader XMLContent;
	
	//private CustomFields customFields = new CustomFields();

	public MessageContent(){
		this.size = 0L;
		this.comment = "";
		this.setRawContent("");
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
	public MessageContent(long size, String mimeType, String rawContent, String encoding, String comment) {
		this.size = size;
		this.mimeType = mimeType;
		this.setRawContent(rawContent);
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
	public String getRawContent() {
		return rawContent;
	}

	/**
	 * Sets the text.
	 * @param text The text to set.
	 */
	public void setRawContent(String rawContent) {
	    if(rawContent != null){
	        rawContent.replace("\"", "\\\"");
	        this.rawContent = rawContent;
	        // Check the content type
	        this.contentType = ContentChecker.checkJsonXmlContent(rawContent);
	        if (ContentType.JSON.equals(contentType)) {
                this.JSONContent = JSONSerializer.toJSON(this.rawContent);
	            this.XMLContent = null;
	        } else if (ContentType.XML.equals(contentType)) {
	            try {
	                XMLInputFactory XMLif=XMLInputFactory.newInstance();
	                this.XMLContent = XMLif.createXMLEventReader(new StringReader(this.rawContent));
	            }
                /*try {
                    DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
                    DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
                    this.XMLContent = docBuilder.parse(new InputSource(new StringReader(this.rawContent)));
                }*/ catch (Exception ex) {
                    ex.printStackTrace();
                    this.contentType = ContentType.Undefined;
                    this.XMLContent = null;    
                }
                this.JSONContent = null;                
	        } else {
	            this.JSONContent = null;
	            this.XMLContent = null;
	        }
	    } else {
	        this.rawContent = "";
	        this.contentType = ContentChecker.ContentType.Undefined;	        
	    }
	}

	/**
	 * Returns the content type
	 * @return The ContentType
	 */
    public ContentType getContentType() {
        return contentType;
    }	
	
	/**
	 * Returns the content as a JSON object. If the raw content is not a JSON structure, null is returned
	 * @return A JSON object if the raw content is a valid JSON structure, null otherwise
	 */
	public JSON getJSONContent(){
	    return this.JSONContent;
	}

	/**
	 * returns the content as a generic XML java object. If the raw content is not an XML structure, null is returned 
	 * @return A generic XML object if the raw content is a valid XML structure, null otherwise
	 */
	public XMLEventReader getXMLContent(){
	    return this.XMLContent;
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
