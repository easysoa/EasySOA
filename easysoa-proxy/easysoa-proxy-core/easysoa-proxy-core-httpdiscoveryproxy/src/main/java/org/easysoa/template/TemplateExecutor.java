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
		InMessage inMessage = (InMessage) JSONObject.toBean(jsonInMessage, InMessage.class, classMap);
		RequestForwarder forwarder = new RequestForwarder();
		OutMessage outMessage =  forwarder.send(inMessage);
		return outMessage;
	}
	
}
