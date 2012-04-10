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
