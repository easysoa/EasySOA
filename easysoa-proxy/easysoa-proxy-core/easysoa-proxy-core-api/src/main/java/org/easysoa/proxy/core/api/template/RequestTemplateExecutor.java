/**
 * 
 */
package org.easysoa.proxy.core.api.template;

import java.util.HashMap;

import org.apache.log4j.Logger;
import org.easysoa.message.CustomField;
import org.easysoa.message.CustomFields;
import org.easysoa.message.Header;
import org.easysoa.message.InMessage;
import org.easysoa.message.OutMessage;
import org.easysoa.message.QueryParam;
import org.easysoa.message.QueryString;
import org.easysoa.proxy.core.api.util.RequestForwarder;

import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;

/**
 * @author jguillemotte
 *
 */
public class RequestTemplateExecutor implements TemplateExecutor {

    // Logger
    private static Logger logger = Logger.getLogger(ResponseTemplateExecutor.class.getName());    
    
    @Override
    public OutMessage execute(String renderedTemplate) throws Exception {
        //logger.debug("Rendered template => " +  renderedTemplate);        
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
