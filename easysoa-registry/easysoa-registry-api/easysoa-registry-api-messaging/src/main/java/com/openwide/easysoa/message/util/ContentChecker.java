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

/**
 * 
 */
package com.openwide.easysoa.message.util;



/**
 * @author jguillemotte
 *
 */
public class ContentChecker {

    // Content type
    public enum ContentType {JSON, XML, Undefined};
    
    /**
     * Check if the content is a JSON structure or an XML structure and returns the corresponding content type
     * @param content The content to check
     * @return The <code>ContentType</code>
     */
    public static ContentType checkJsonXmlContent(String content){
        if(content == null){
            content = "";
        }
        // JSON content
        if(content.startsWith("{") && content.endsWith("}")){
            return ContentType.JSON;
        } 
        // XML content
        else if(content.startsWith("<") && content.endsWith(">")) {
            return ContentType.XML;
        }
        // Undefined content
        else {
            return ContentType.Undefined;
        }
    }
    
}
