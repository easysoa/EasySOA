/**
 * 
 */
package org.easysoa.template.setters;

import java.util.List;
import java.util.Map;
import org.easysoa.template.TemplateField;
import com.openwide.easysoa.message.InMessage;

/**
 * @author jguillemotte
 *
 */
public interface CustomParamSetter {

	/**
	 * Check if the CustomParamSetter is able  
	 * @return
	 */
	public boolean isOkFor(TemplateField templateField);
	
	/**
	 * Set the param
	 * @param templateField 
	 * @param inMessage
	 * @param mapParams
	 */
	public void setParam(TemplateField templateField, InMessage inMessage, Map<String, List<String>> Params);
	
}
