/**
 * 
 */
package org.easysoa.template.parsers;

import java.util.HashMap;

import org.easysoa.records.correlation.CandidateField;
import org.w3c.dom.Document;

import com.openwide.easysoa.message.OutMessage;

/**
 * @author jguillemotte
 *
 */
public class XMLParser implements TemplateParser {

    @Override
    public boolean canParse(OutMessage outMessage) {
        return outMessage.getMessageContent().isXMLContent();
    }    
    
    @Override
    public HashMap<String, CandidateField> parse(OutMessage outMessage, HashMap<String, CandidateField> fieldMap) {
        Document content = outMessage.getMessageContent().getXMLContent();
        // Get each field from the XML content and add it in the HashMap ....
        // TODO : add code to complete this method
        
        return null;
    }

}
