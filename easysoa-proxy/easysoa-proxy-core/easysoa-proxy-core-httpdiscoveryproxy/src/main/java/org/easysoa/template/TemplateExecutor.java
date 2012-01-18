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
	 * Execute a rendered template and returns the response as a JSON String
	 * @param renderedTemplate The rendered template to execute
	 * @return The response a JSON String
	 * @throws Exception If a problem occurs
	 */
	public String execute(String renderedTemplate) throws Exception {
		// Call the replay service
		JSONObject jsonInMessage = (JSONObject) JSONSerializer.toJSON(renderedTemplate);
		HashMap<String, Class> classMap = new HashMap<String, Class>();
		classMap.put("headers", Header.class);
		classMap.put("headerList", Header.class);
		classMap.put("customFields", CustomFields.class);
		classMap.put("customFieldList", CustomField.class);		
		InMessage inMessage = (InMessage) JSONObject.toBean(jsonInMessage, InMessage.class, classMap);
		RequestForwarder forwarder = new RequestForwarder();
		OutMessage outMessage =  forwarder.send(inMessage);
		JSONObject jsonOutMessage = JSONObject.fromObject(outMessage);
		return jsonOutMessage.toString();
	}
	
}
