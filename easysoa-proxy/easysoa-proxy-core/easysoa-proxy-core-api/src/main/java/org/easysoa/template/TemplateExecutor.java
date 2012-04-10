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
package org.easysoa.template;

import java.util.HashMap;

import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;
import com.openwide.easysoa.message.CustomField;
import com.openwide.easysoa.message.CustomFields;
import com.openwide.easysoa.message.Header;
import com.openwide.easysoa.message.InMessage;
import com.openwide.easysoa.message.OutMessage;
import com.openwide.easysoa.message.QueryParam;
import com.openwide.easysoa.message.QueryString;
import com.openwide.easysoa.util.RequestForwarder;

/**
 * Execute a rendered template
 * 
 * @author jguillemotte
 *
 */
public class TemplateExecutor {

	/**
	 * Execute a rendered template and returns the response as an <code>OutMessage</code>
	 * @param renderedTemplate The rendered template to execute
	 * @return The response as an <code>OutMessage</code>
	 * @throws Exception If a problem occurs
	 */
	public OutMessage execute(String renderedTemplate) throws Exception {
		// Call the replay service
		JSONObject jsonInMessage = (JSONObject) JSONSerializer.toJSON(renderedTemplate);
		System.out.println("JSONInMessage : " + jsonInMessage);
		HashMap<String, Class> classMap = new HashMap<String, Class>();
		classMap.put("headers", Header.class);
		classMap.put("headerList", Header.class);
		classMap.put("customFields", CustomFields.class);
		classMap.put("customFieldList", CustomField.class);
		classMap.put("queryString", QueryString.class);
		classMap.put("queryParams", QueryParam.class);
		InMessage inMessage = (InMessage) JSONObject.toBean(jsonInMessage, InMessage.class, classMap);
		RequestForwarder forwarder = new RequestForwarder();
		OutMessage outMessage =  forwarder.send(inMessage);
        // TODO : call the replay engine instead of the forwarder directly. We have to plug the assertion engine on the replay engine		
        //ExchangeReplayServiceImpl replayService = new ExchangeReplayServiceImpl();
        //replayService.replay(exchangeRecordStoreName, exchangeRecordId);
		return outMessage;
	}
	
}
