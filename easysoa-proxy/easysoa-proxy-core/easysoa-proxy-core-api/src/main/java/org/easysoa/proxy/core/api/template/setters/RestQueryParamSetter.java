/**
 * EasySOA Proxy
 * Copyright 2011 Open Wide
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * 
 * Contact : easysoa-dev@googlegroups.com
 */

package org.easysoa.proxy.core.api.template.setters;

import java.util.List;
import java.util.Map;
import org.apache.log4j.Logger;
import org.easysoa.message.InMessage;
import org.easysoa.message.QueryParam;
import org.easysoa.proxy.core.api.template.AbstractTemplateField;
import org.easysoa.proxy.core.api.template.AbstractTemplateField.TemplateFieldType;


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
