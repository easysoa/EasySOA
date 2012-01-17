/**
 * 
 */
package org.easysoa.template;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;

import org.apache.http.client.ClientProtocolException;
import org.apache.log4j.Logger;
import org.easysoa.records.ExchangeRecord;
import org.osoa.sca.annotations.Reference;

import com.openwide.easysoa.message.CustomField;
import com.openwide.easysoa.message.CustomFields;
import com.openwide.easysoa.message.Header;
import com.openwide.easysoa.message.InMessage;
import com.openwide.easysoa.message.OutMessage;
import com.openwide.easysoa.util.RequestForwarder;

/**
 * @author jguillemotte
 *
 */
public class TemplateRenderer implements TemplateProcessorRendererItf {

	// Logger
	private static Logger logger = Logger.getLogger(TemplateRenderer.class.getName());	
	
	@Reference(required = true)
    protected TemplateRendererItf template;
	
	/**
	 * Default constructor
	 */
	public TemplateRenderer(){
	}
	
	/**
	 * Render the template to an exploitable HTML form
	 * @param recordTemplate
	 * @throws IOException 
	 * @throws ClientProtocolException 
	 */
	public String renderReq(String templatePath, ExchangeRecord record, Map<String, String> fieldValues) throws Exception {
		/**
		The TemplateRenderer executes a record (request) template by loading its record and rendering 
		it in the chosen template engine, with template variables set to user provided values first, 
		doing the corresponding request call and returning its result. Note that the TemplateRenderer 
		can therefore be used (or tested) with hand-defined exchange (request) templates.
		*/
		logger.debug("Passing in renderReq method !!!");

		// Render the template
		String renderedTemplate = template.renderReq(templatePath, fieldValues);
		logger.debug("Rendered template : " + renderedTemplate);
		
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

	public String renderRes(String templatePath, ExchangeRecord record, Map<String, String> fieldValues){
		// TODO : Complete this method, to be used in a server mock
		logger.warn("renderRes method not yet entierely implemented, need to be completed !");
		String renderedTemplate = template.renderRes(templatePath, fieldValues);
		return renderedTemplate;
	}
	
}
