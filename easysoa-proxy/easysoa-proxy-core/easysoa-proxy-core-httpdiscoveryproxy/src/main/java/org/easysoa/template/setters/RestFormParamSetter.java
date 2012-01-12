/**
 * 
 */
package org.easysoa.template.setters;

import java.util.StringTokenizer;

import javax.ws.rs.core.MultivaluedMap;

import org.apache.log4j.Logger;
import org.easysoa.template.TemplateField;
import org.easysoa.template.TemplateField.TemplateFieldType;

import com.openwide.easysoa.message.InMessage;

/**
 * @author jguillemotte
 *
 */
public class RestFormParamSetter implements CustomParamSetter {

	// Logger
	private static Logger logger = Logger.getLogger(RestFormParamSetter.class.getName());
	
	/* (non-Javadoc)
	 * @see org.easysoa.template.CustomParamSetter#isOkFor()
	 */
	@Override
	public boolean isOkFor(TemplateField templateField) {
		if(TemplateFieldType.CONTENT_PARAM.equals(templateField.getParamType())){
			return true;
		} else {
			return false;			
		}
	}

	/* (non-Javadoc)
	 * @see org.easysoa.template.CustomParamSetter#setParams()
	 */
	@Override
	public void setParam(TemplateField templateField, InMessage inMessage, MultivaluedMap<String, String> mapParams) {
		logger.debug("Set form param for " + templateField.getFieldName());
		// get message content
		// Params are stored like queryParams : param=value&param=value ...
		// cut the content with a stringTokenizer, each token represent a param with a value
		// replace the substring from = to end		
		//inMessage.getMessageContent().setContent(ParamTokenizer.replaceParamValues(templateField.getFieldName(), inMessage.getMessageContent().getContent(), mapParams));
		StringBuffer buffer = new StringBuffer();
		StringTokenizer tokenizer = new StringTokenizer(inMessage.getMessageContent().getContent(), "&");
		String newValue;
		int index = 0;
		while(tokenizer.hasMoreElements()){
			String token = tokenizer.nextToken();
			if(buffer.length() > 0){
				buffer.append("&");
			}
			if(token.startsWith(templateField.getFieldName())){
				newValue = token.substring(0, token.lastIndexOf("=")) + mapParams.get(templateField.getFieldName()).get(index);
				index++;
				buffer.append(newValue);
			} else {
				buffer.append(token);
			}
		}
		inMessage.getMessageContent().setContent(buffer.toString());
	}

}
