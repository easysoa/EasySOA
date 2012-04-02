/**
 * 
 */
package org.easysoa.template.setters;

import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import org.apache.log4j.Logger;
import org.easysoa.template.AbstractTemplateField;
import org.easysoa.template.AbstractTemplateField.TemplateFieldType;
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
	public boolean isOkFor(AbstractTemplateField templateField) {
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
	public void setParam(AbstractTemplateField templateField, InMessage inMessage, Map<String, List<String>> params) {
		logger.debug("Set form param for " + templateField.getFieldName());
		// get message content
		// Params are stored like queryParams : param=value&param=value ...
		// cut the content with a stringTokenizer, each token represent a param with a value
		// replace the substring from = to end		
		//inMessage.getMessageContent().setContent(ParamTokenizer.replaceParamValues(templateField.getFieldName(), inMessage.getMessageContent().getContent(), mapParams));
		StringBuffer buffer = new StringBuffer();
		StringTokenizer tokenizer = new StringTokenizer(inMessage.getMessageContent().getRawContent(), "&");
		String newValue;
		int index = 0;
		while(tokenizer.hasMoreElements()){
			String token = tokenizer.nextToken();
			if(buffer.length() > 0){
				buffer.append("&");
			}
			if(token.startsWith(templateField.getFieldName())){
				newValue = token.substring(0, token.lastIndexOf("=")) + params.get(templateField.getFieldName()).get(index);
				index++;
				buffer.append(newValue);
			} else {
				buffer.append(token);
			}
		}
		inMessage.getMessageContent().setRawContent(buffer.toString());
	}

}
