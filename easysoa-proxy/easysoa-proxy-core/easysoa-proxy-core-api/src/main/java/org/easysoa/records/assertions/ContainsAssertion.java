/**
 * 
 */
package org.easysoa.records.assertions;

import java.util.Iterator;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.XMLEvent;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

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
    public AssertionResult check(String fieldName, OutMessage originalMessage, OutMessage replayedMessage) {
        AssertionResult result;
        // FieldName is specified : searching the field value and make the assertion only on this value
        if(fieldName != null && !"".equals(fieldName)){
            // JSON
            MessageContent originalMessageContent = originalMessage.getMessageContent();
            if(ContentType.JSON.equals(originalMessageContent.getContentType())){
                // Call a JSON parser  to transform the JSON content to Java structure (Java structure with generic JSON objects)
                // or extract substring corresponding to the field value and compare them (scrap method) .... ??
                reportLogger.info("Getting original value for field : " + findJSONFieldValue(fieldName, originalMessageContent.getJSONContent()));
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
                //Element rootElement = originalMessageContent.getXMLContent().getDocumentElement();
                String originalXmlFieldValue = findXMLFieldValue(fieldName, originalMessageContent.getXMLContent());
                reportLogger.info("Getting original value for field : " + originalXmlFieldValue);
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

    /**
     * Search and returns the value corresponding to the specified field
     * @param json
     * @return
     */
    // TODO throws an exception if the field is not found
    // Pb : how to get a part of the JSON structure ..... 
    private String findJSONFieldValue(String referenceField, JSON json){
        if(json instanceof JSONObject){
            // Get all the key contained in the object and for each key, get the associated object and call this recursive method
            //logger.debug("Instance of JSONObject found");
            JSONObject jObject = (JSONObject) json;
            if(jObject.containsKey(referenceField)){
                String objectString = jObject.getString(referenceField);
                if(objectString == null){
                    objectString = jObject.getJSONObject(referenceField).toString();
                }
                return objectString;
            } else {
                // for each sub object
                Iterator<String> keyIterator = jObject.keys();
                while(keyIterator.hasNext()){
                    JSON jsonObj = (JSON)jObject.get(keyIterator.next());
                    findJSONFieldValue(referenceField, jsonObj);
                }
            }
        } else if(json instanceof JSONArray) {
            // For each JSONObject contained in the array, call this recursive method
            //logger.debug("Instance of JSONArray found");
            JSONArray jsonArray = (JSONArray) json;
            for(Object object : jsonArray){
                JSON jsonObj = (JSON) object;
                findJSONFieldValue(referenceField, jsonObj);
            }
        }
        return null;
    }
    
    /**
     * 
     * @param xml
     * @return
     */
    // TODO throws an exception if the field is not found    
    private String findXMLFieldValue(String referenceField, XMLEventReader eventReader){
        // Checking the whole document to find the element corresponding to the referenceField
        while(eventReader.hasNext()){
            try {
                XMLEvent xmlEvent = eventReader.nextEvent();
                if(XMLEvent.START_ELEMENT == xmlEvent.getEventType()){
                    if(referenceField.equals(xmlEvent.asStartElement().getName().getLocalPart())){
                        //return xmlEvent.asStartElement().
                        return null;
                    }
                }
              //clean up
                eventReader.close();
            } catch (XMLStreamException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            
        }
        return null;
    }
    
}
