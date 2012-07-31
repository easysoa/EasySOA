/**
 * EasySOA Proxy Core
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

package org.easysoa.records.correlation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.StringTokenizer;
import org.apache.log4j.Logger;
import org.easysoa.message.InMessage;
import org.easysoa.message.OutMessage;
import org.easysoa.message.QueryParam;
import org.easysoa.template.parsers.JSONParser;
import org.easysoa.template.parsers.TemplateParser;
import org.easysoa.template.parsers.XMLParser;


/**
 * Field extractor : extract params from input and output and returns CandidateFields to use with correlation engine
 * 
 * @author jguillemotte
 *
 */
public class FieldExtractor {

    // Logger
    private static Logger logger = Logger.getLogger(FieldExtractor.class.getName());
    
    /**
     * Extract params from the url path
     * @param inMessage
     * @return An <code>HashMap</code> filled with query parameters
     */
    public HashMap<String, CandidateField> getInputPathParams(InMessage inMessage){
        HashMap<String,CandidateField> fieldMap = new HashMap<String,CandidateField>();
        CandidateField field;
        StringTokenizer tokenizer = new StringTokenizer(inMessage.getPath(), "/");
        int pathPos = 0;
        while(tokenizer.hasMoreTokens()){
            String token = tokenizer.nextToken();
            field = new CandidateField("path." + pathPos, token);
            field.setKind("IN_PATH_PARAM");
            fieldMap.put("path." + pathPos, field);
            pathPos++;
        }
        logger.debug("Path param fields map : " + fieldMap);
        return fieldMap;
    }
    
    /**
     * Take the query params from <code>InMessage</code> and fill an HashMap with them. 
     * @param inMessage <code>InMessage</code> containing query params
     * @return An <code>HashMap</code> filled with query parameters 
     */
    public HashMap<String, CandidateField> getInputQueryParams(InMessage inMessage){
        HashMap<String,CandidateField> fieldMap = new HashMap<String,CandidateField>();
        CandidateField candidateField;
        for(QueryParam queryParam : inMessage.getQueryString().getQueryParams()){
            candidateField = new CandidateField(queryParam.getName(), queryParam.getValue());
            candidateField.setKind("IN_QUERY_PARAM");
            fieldMap.put(candidateField.getPath(), candidateField);
        }
        logger.debug("Query param fields map : " + fieldMap);       
        return fieldMap;
    }
    
    /**
     * Take the content params (eg : HTML form POST params) from <code>InMessage</code> and fill an HashMap with them. 
     * @param inMessage <code>InMessage</code> containing query params
     * @return An <code>HashMap</code> filled with query parameters
     */
    // TODO : Change this code .... For REST request, content params are processed as query params
    // For SOAP request, need to add code to parse the xml. 
    public HashMap<String, CandidateField> getInputContentParam(InMessage inMessage){
        HashMap<String,CandidateField> fieldMap = new HashMap<String,CandidateField>();
        CandidateField candidateField;
        StringTokenizer tokenizer = new StringTokenizer(inMessage.getMessageContent().getRawContent(), "&");
        String token;
        while(tokenizer.hasMoreTokens()){
            token = tokenizer.nextToken();
            candidateField = new CandidateField(token.substring(0, token.indexOf("=")), token.substring(token.indexOf("=")));
            candidateField.setKind("IN_CONTENT_PARAM");
            fieldMap.put(candidateField.getPath(), candidateField);
        }
        logger.debug("Content param fields map : " + fieldMap);
        return fieldMap;
    }    
    
    /**
     * 
     * @param outMessage
     * @return
     */
    public HashMap<String, CandidateField> getOutputFields(OutMessage outMessage){
        HashMap<String,CandidateField> fieldMap = new HashMap<String,CandidateField>();
        // TODO add code here to fill hashmap with output fields
        // Message content can be very different (json, xml ...)
        // TODO Add a system to choose the parser corresponding to the out message content
        // Today only a json parser for out message
        logger.debug("Output field extraction ...");
        List<TemplateParser> templateParserList = new ArrayList<TemplateParser>();
        templateParserList.add(new JSONParser());
        templateParserList.add(new XMLParser());
        // TODO : Add a parser for simple text content
        try{
            for(TemplateParser parser : templateParserList){
                if(parser.canParse(outMessage)){
                    parser.parse(outMessage, fieldMap);
                    break;
                }
            }
            // If message content is a JSON
            /*if(outMessage.getMessageContent().isJSONContent()){
                JSONParser jsonParser = new JSONParser();
                jsonParser.parse(outMessage, fieldMap);
            } 
            // if message structure is an XML structure
            else if(outMessage.getMessageContent().isXMLContent()){
                XMLParser xmlParser = new XMLParser();
                xmlParser.parse(outMessage, fieldmap);
            }*/
        }
        catch(Exception ex){
            logger.warn("An error occurs during the output fields extraction", ex);
        }
        logger.debug("Out param fields map : " + fieldMap);     
        return fieldMap;
    }    
    
}
