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

package org.easysoa.proxy.core.api.template;

import net.sf.json.JSONObject;
import org.easysoa.records.ExchangeRecord;

/**
 * @author jguillemotte
 *
 */
public class VelocityTemplate {

        public final static String VELOCIMACRO_REQUEST_PREFIX = "#macro ( execute_custom $arg0 $arg1 $arg2 )";
        public final static String VELOCIMACRO_RESPONSE_PREFIX = "#macro ( execute_custom $arg0 $arg1 $arg2 )";
	public final static String VELOCIMACRO_SUFFIX = "#end";

        // Echange record
	private ExchangeRecord customRecord;
	
        /**
         * Build a velocity template
         * @param customRecord 
         */
	public VelocityTemplate(ExchangeRecord customRecord){
		this.customRecord = customRecord;
	}
	
        /**
         * 
         * @return 
         */
	public String getRequestTemplate(){
		JSONObject jsonRecord = JSONObject.fromObject(customRecord.getInMessage());
		String jsonString;
		if(customRecord.getInMessage().getMessageContent().isXMLContent()){
		    // If the request raw content contains SOAP, no need to remove escaped characters
		    jsonString = jsonRecord.toString();
		} else {
		    // Otherwise, if it contains JSON raw content, we have to replace escaped characters in template expression to avoid an error
                    jsonString = jsonRecord.toString().replace("\\\"", "\"");		    
		}
		return VELOCIMACRO_REQUEST_PREFIX + "\n" + jsonString + "\n" + VELOCIMACRO_SUFFIX; 
	}
	
        /**
         * 
         * @return 
         */
	public String getrecordID(){
		return this.customRecord.getExchange().getExchangeID();
	}
	
        /**
         * 
         * @return 
         */
	public String getResponsetemplate(){
		JSONObject jsonRecord = JSONObject.fromObject(customRecord.getOutMessage());
		return VELOCIMACRO_RESPONSE_PREFIX + "\n" + jsonRecord.toString() + "\n" + VELOCIMACRO_SUFFIX;
	}
}
