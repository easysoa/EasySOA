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

package org.easysoa.proxy.core.api.records.assertions;

import java.util.List;

import org.easysoa.message.OutMessage;
import org.easysoa.proxy.core.api.template.TemplateFieldSuggestions;


public interface AssertionEngine {

    /**
     * Suggest assertions and store them in a 'asr' file.
     * @param fieldSuggestions
     * @param recordID
     * @param storeName
     * @return AssertionSuggestions
     * @throws Exception If a problem occurs during the creation of the 'asr' file
     */
    public abstract AssertionSuggestions suggestAssertions(TemplateFieldSuggestions fieldSuggestions, String recordID, String storeName) throws Exception;

    /**
     * Executes several assertions
     * @param assertionsSuggestions The assertions to execute. If this parameter is null, default assertion suggestions will be used. See AssertionSuggestionService class.
     * @return A <code>List</code> of <code>AssertionResult</code> 
     */
    public abstract List<AssertionResult> executeAssertions(AssertionSuggestions assertionSuggestions, OutMessage originalMessage, OutMessage replayedMessage);

    /**
     * Execute one assertion
     * @param referenceField The reference field
     * @param assertion The assertion to execute
     * @return The <code>AssertionResult</code>
     * @throws Exception If a problem occurs during the assertion execution
     */
    public abstract AssertionResult executeAssertion(String referenceField, Assertion assertion, OutMessage originalMessage, OutMessage replayedMessage);

}