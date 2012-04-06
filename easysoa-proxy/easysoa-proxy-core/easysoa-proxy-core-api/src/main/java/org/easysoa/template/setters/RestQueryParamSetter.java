package org.easysoa.template.setters;

import java.util.List;
import java.util.Map;
import org.apache.log4j.Logger;
import org.easysoa.template.AbstractTemplateField;
import org.easysoa.template.AbstractTemplateField.TemplateFieldType;

import com.openwide.easysoa.message.InMessage;
import com.openwide.easysoa.message.QueryParam;

public class RestQueryParamSetter implements CustomParamSetter {

	// Logger
	private static Logger logger = Logger.getLogger(RestQueryParamSetter.class.getName());
	
	/* (non-Javadoc)
	 * @see org.easysoa.template.CustomParamSetter#isOkFor()
	 */
	@Override
	public boolean isOkFor(AbstractTemplateField templateField) {
		if(TemplateFieldType.IN_QUERY_PARAM.equals(templateField.getParamType())){
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
		logger.debug("Set query param for " + templateField.getFieldName());
		int index = 0;
		for(QueryParam param : inMessage.getQueryString().getQueryParams()){
			if(param.getName().equals(templateField)){
				param.setValue(params.get(templateField.getFieldName()).get(index));
				index++;
			}
		}
	}

}
