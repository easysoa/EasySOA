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

import com.openwide.easysoa.message.OutMessage;

/**
 * 
 * @author jguillemotte
 */
public interface Assertion {

    /**
     * Returns the Assertion ID
     * @return The Assertion ID
     */
    public String getID();
    
    /**
     * Set assertion configuration as JSON String or XML String
     * @param configurationString The configuration as a JSON or XML structure
     */
    // TODO define the JSON or XML configuration structure
    public void setConfiguration(String configurationString);
    
    /**
     * Check the assertion
     * @param fieldName The field on which the assertion will be done. 
     * If this parameter is empty or null, the assertion will be executed on the entire message content.
     * @param originalMessage Original message
     * @param replayedMessage ReplayedMessage
     * @return An AssertionResult
     */
    public AssertionResult check(String fieldName, OutMessage originalMessage, OutMessage replayedMessage)/* throws Exception*/;
    
}
