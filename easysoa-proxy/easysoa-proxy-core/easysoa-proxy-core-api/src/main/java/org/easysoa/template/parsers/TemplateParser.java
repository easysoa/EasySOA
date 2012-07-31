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

import org.easysoa.message.OutMessage;
import org.easysoa.records.correlation.CandidateField;


/**
 * @author jguillemotte
 *
 */
public interface TemplateParser {

    /**
     * Parse a JSON content to generate a fieldMap containing output fields
     * @param outMessage The out message to parse
     * @param fieldMap The field map to fill
     * @return
     */
    public HashMap<String, CandidateField> parse(OutMessage outMessage, HashMap<String, CandidateField> fieldMap);

    /**
     * Return true if the message content can be parsed by this parser
     * @param outMessage The message to parse
     * @return true if this parser can parse the message content
     */
    public boolean canParse(OutMessage outMessage);
    
}
