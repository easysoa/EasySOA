/**
 * EasySOA HTTP Proxy
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
package org.easysoa.records.assertions;

import java.io.StringWriter;
import java.util.Iterator;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;
import net.sf.json.JSON;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

/**
 * @author jguillemotte
 *
 */
public abstract class AbstractAssertion implements Assertion {

    // Assertion id
    protected String id;
    
    /**
     * Default constructor
     * @param id Id or unique name for the assertion
     */
    public AbstractAssertion(String id) {
        this.id = id;
    }
    
    @Override
    public String getID(){
        return this.id;
    }
    
    /**
     * Search and returns the value corresponding to the specified field in a JSON Structure
     * @param json The JSON structure where the field will be searched
     * @return The field value
     */
    public String findJSONFieldValue(String referenceField, JSON json){
        if(json instanceof JSONObject){
            // Get all the key contained in the object and for each key, get the associated object and call this recursive method
            JSONObject jObject = (JSONObject) json;
            if(jObject.containsKey(referenceField)){
                String objectString = jObject.getString(referenceField);
                /*if(objectString == null){
                    objectString = jObject.getJSONObject(referenceField).toString();
                }*/
                return objectString;
            } else {
                // for each sub object
                @SuppressWarnings("unchecked")
                Iterator<String> keyIterator = jObject.keys();
                while(keyIterator.hasNext()){
                    JSON jsonObj = (JSON)jObject.get(keyIterator.next());
                    findJSONFieldValue(referenceField, jsonObj);
                }
            }
        } else if(json instanceof JSONArray) {
            // For each JSONObject contained in the array, call this recursive method
            JSONArray jsonArray = (JSONArray) json;
            for(Object object : jsonArray){
                JSON jsonObj = (JSON) object;
                findJSONFieldValue(referenceField, jsonObj);
            }
        }
        return null;
    }
    
    // TODO throws an exception if the field is not found    
    /*private String findXMLFieldValue(String referenceField, XMLEventReader eventReader){
        // Checking the whole document to find the element corresponding to the referenceField
        String fieldValue = null;
        try {
            while(eventReader.hasNext()){
                XMLEvent xmlEvent = eventReader.nextEvent();
                reportLogger.debug("Event type = " + xmlEvent.getEventType());
                if(XMLEvent.START_ELEMENT == xmlEvent.getEventType()){
                    StartElement startElement = xmlEvent.asStartElement();
                    reportLogger.debug("element name : " + startElement.getName().getLocalPart());
                    if(referenceField.equals(startElement.getName().getLocalPart())){
                        // For element containing text
                        if(eventReader.peek().isEndDocument()){
                            fieldValue = eventReader.getElementText();
                        }
                        // For element containing a complex structure                        
                        else {
                            // get the entire structure and returns it as a String
                            // How to do with stax .... seem's to be impossible or very hard ...
                        }
                    }
                }
            }
            //clean up
            eventReader.close();
        } catch (XMLStreamException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
         }
         return fieldValue;
    }*/
    
    /**
     * Search and returns the value corresponding to the specified field in a XML document
     * @param referenceField The field name
     * @param doc The XML document where the field will be searched
     * @return The field value
     */
    public String findXMLFieldValue(String referenceField, Document doc) throws Exception {
        XPathFactory xpathFactory = XPathFactory.newInstance();
        XPath xpath = xpathFactory.newXPath();
        String expression = "//" + referenceField + "/descendant::*";
        Node node = (Node) xpath.evaluate(expression, doc, XPathConstants.NODE);
        return nodeToString(node);
    }
    
    /**
     * Convert a Node in String
     * @param node The node to convert
     * @return The XML string representation of the node
     */
    public String nodeToString(Node node) throws Exception {
        StringWriter sw = new StringWriter();
        Transformer t = TransformerFactory.newInstance().newTransformer();
        t.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
        t.transform(new DOMSource(node), new StreamResult(sw));
        return sw.toString();
    }    
    
}
