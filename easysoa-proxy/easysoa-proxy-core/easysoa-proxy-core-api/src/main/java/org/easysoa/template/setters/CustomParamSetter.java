/**
 * 
 */
package org.easysoa.template.setters;

import java.util.List;
import java.util.Map;
import org.easysoa.template.TemplateField;
import com.openwide.easysoa.message.InMessage;

/**
 * Param setter interface
 * 
 * @author jguillemotte
 *
 */
@Deprecated
// Used in previous templateing system, not used in the new System. The template 'Velocity' expressions are replaced by provided values in the velocity engine.
// This model can be used in the new system with few modifications to build the velocity expressions (see TemplateBuilder class).
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
