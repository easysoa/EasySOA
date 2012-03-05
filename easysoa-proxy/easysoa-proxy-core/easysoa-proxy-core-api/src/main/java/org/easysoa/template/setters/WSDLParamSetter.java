package org.easysoa.template.setters;

import java.util.List;
import java.util.Map;
import org.apache.log4j.Logger;
import org.easysoa.template.TemplateField;
import org.easysoa.template.TemplateField.TemplateFieldType;
import com.openwide.easysoa.message.InMessage;

/**
 * 
 * @author jguillemotte
 *
 */
public class WSDLParamSetter implements CustomParamSetter {

	// Logger
	private static Logger logger = Logger.getLogger(WSDLParamSetter.class.getName());
	
	/* (non-Javadoc)
	 * @see org.easysoa.template.CustomParamSetter#isOkFor()
	 */
	@Override
	public boolean isOkFor(TemplateField templateField) {
		if(TemplateFieldType.WSDL_PARAM.equals(templateField.getParamType())){
			return true;
		} else {
			return false;
		}
	}

	/* (non-Javadoc)
	 * @see org.easysoa.template.CustomParamSetter#setParams()
	 */
	@Override
	public void setParam(TemplateField templateField, InMessage inMessage, Map<String, List<String>> params) {
		logger.debug("Set WSDL param for " + templateField.getFieldName());
		// Use the mechanism developped for scaffolder proxy (with easysoa)
	}

}
