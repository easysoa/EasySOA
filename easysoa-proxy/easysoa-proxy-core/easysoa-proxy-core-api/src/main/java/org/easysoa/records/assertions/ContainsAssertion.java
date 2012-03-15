/**
 * 
 */
package org.easysoa.records.assertions;

import java.io.StringWriter;
import java.util.Iterator;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import net.sf.json.JSON;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import com.openwide.easysoa.message.MessageContent;
import com.openwide.easysoa.message.OutMessage;
import com.openwide.easysoa.message.util.ContentChecker.ContentType;

/**
 * 
 * 
 * @author jguillemotte
 *
 */ 
public class ContainsAssertion extends AbstractAssertion {

    private static final Logger reportLogger = Logger.getLogger("reportLogger");    
    
    /**
     * Constructor
     * @param id Assertion ID
     */
    public ContainsAssertion(String id) {
        super(id);
    }

    @Override
    public void setConfiguration(String configurationString) {
        // TODO Auto-generated method stub
    }

    // 2 methods : 
    //- first extract a substring containing the field name and it's value from original message
    // and check if this substring is contained in the replayed message
    // Or 
    // Parse a JSON / XML structure to have a java object structure
    // And with recursive function, find the field and it associated value .. and check if we have the same field in the replayed structure
    @Override
    public AssertionResult check(String fieldName, OutMessage originalMessage, OutMessage replayedMessage) throws Exception {
        AssertionResult result;
        // FieldName is specified : searching the field value and make the assertion only on this value
        if(fieldName != null && !"".equals(fieldName)){
            // JSON
            MessageContent originalMessageContent = originalMessage.getMessageContent();
            if(ContentType.JSON.equals(originalMessageContent.getContentType())){
                // Call a JSON parser  to transform the JSON content to Java structure (Java structure with generic JSON objects)
                // or extract substring corresponding to the field value and compare them (scrap method) .... ??
                reportLogger.info("Getting original value for field " + fieldName + " : " + findJSONFieldValue(fieldName, originalMessageContent.getJSONContent()));
                String originalJsonFieldValue = findJSONFieldValue(fieldName, originalMessageContent.getJSONContent());
                if(originalJsonFieldValue != null){
                    if(originalJsonFieldValue.contains(findJSONFieldValue(fieldName, replayedMessage.getMessageContent().getJSONContent()))){
                        result = new AssertionResult(this.getClass(), AssertionResult.AssertionResultStatus.OK);
                    } else {
                        result = new AssertionResult(this.getClass(), AssertionResult.AssertionResultStatus.KO);                        
                    }
                } else {
                    result = new AssertionResult(this.getClass(), AssertionResult.AssertionResultStatus.KO, "Field " + fieldName + "was not found in the original message content" );
                }
            }
            // XML
            else if(ContentType.XML.equals(originalMessageContent.getContentType())) {
                String originalXmlFieldValue = findXMLFieldValue(fieldName, originalMessageContent.getXMLContent());
                reportLogger.info("Getting original value for field " + fieldName + " : " + originalXmlFieldValue);
                if(originalXmlFieldValue != null){
                    if(originalXmlFieldValue.contains(findXMLFieldValue(fieldName, replayedMessage.getMessageContent().getXMLContent()))){
                        result = new AssertionResult(this.getClass(), AssertionResult.AssertionResultStatus.OK);
                    } else {
                        result = new AssertionResult(this.getClass(), AssertionResult.AssertionResultStatus.KO);                        
                    }
                } else {
                    result = new AssertionResult(this.getClass(), AssertionResult.AssertionResultStatus.KO, "Field " + fieldName + "was not found in the original message content" );
                }
            }
            // Undefined
            else {
                // How to get the field and it's value ....
                // At the moment simply returns a KO result
                result = new AssertionResult(this.getClass(), AssertionResult.AssertionResultStatus.KO, "Message content is not JSON or XML, unable to find the field " + fieldName);
            }
        }
        // No fieldName specified : making assertion on the whole message content
        else {
            if(originalMessage.getMessageContent().getRawContent().contains(replayedMessage.getMessageContent().getRawContent())){
                result = new AssertionResult(this.getClass(), AssertionResult.AssertionResultStatus.OK);
                result.addMetric("Contains assertion method", String.valueOf(true), originalMessage.getMessageContent().getRawContent(), replayedMessage.getMessageContent().getRawContent());            
            } else {
                result = new AssertionResult(this.getClass(), AssertionResult.AssertionResultStatus.KO);
                result.addMetric("Contains assertion method", String.valueOf(false), originalMessage.getMessageContent().getRawContent(), replayedMessage.getMessageContent().getRawContent());
            }
        }
        return result;
    }
    
}
