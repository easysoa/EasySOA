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

/**
 * 
 */
package org.easysoa.template.parsers;

import java.util.HashMap;

import org.apache.log4j.Logger;
import org.easysoa.records.correlation.CandidateField;
import org.easysoa.records.correlation.FieldExtractor;
import org.w3c.dom.Document;

import com.openwide.easysoa.message.OutMessage;

/**
 * @author jguillemotte
 *
 */
public class XMLParser implements TemplateParser {

    // Logger
    private static Logger logger = Logger.getLogger(XMLParser.class.getName());    
    
    @Override
    public boolean canParse(OutMessage outMessage) {
        logger.debug("Out message contains XML => " + outMessage.getMessageContent().isXMLContent());
        return outMessage.getMessageContent().isXMLContent();
    }    
    
    @Override
    public HashMap<String, CandidateField> parse(OutMessage outMessage, HashMap<String, CandidateField> fieldMap) {
        logger.debug("Parsing XML message");
        Document content = outMessage.getMessageContent().getXMLContent();
        // TODO Add code to extract out fields

        // How to parse the message and extract the output fields ...
        
        return fieldMap;
    }

}
