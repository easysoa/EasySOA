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
