/**
 * EasySOA Proxy core
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

package org.easysoa.proxy.core.api.records.correlation;

import java.util.HashMap;

import org.easysoa.proxy.core.api.template.TemplateFieldSuggestions;
import org.easysoa.records.ExchangeRecord;

/**
 * Correlation engine interface for SCA component
 * 
 * @author jguillemotte
 *
 */
public interface CorrelationEngine {

    /**
     * Returns a list of template fields found by a correlation mechanism
     * @param jsonExchange
     * @param inPathFields
     * @param inQueryFields
     * @param inContentFields
     * @param foundOutFields
     * @return
     */
    // TODO : The input / output field extraction must be done in the correlation engine
    public TemplateFieldSuggestions correlateWithSubpath(ExchangeRecord jsonExchange, HashMap<String, CandidateField> inPathFields,
            HashMap<String, CandidateField> inQueryFields,
            HashMap<String, CandidateField> inContentFields,
            HashMap<String, CandidateField> foundOutFields);
    
}
