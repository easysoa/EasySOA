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

/**
 * 
 */
package org.easysoa.template.setters;

import java.util.List;
import java.util.Map;
import org.easysoa.template.AbstractTemplateField;
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
	public boolean isOkFor(AbstractTemplateField templateField);
	
	/**
	 * Set the param
	 * @param templateField
	 * @param inMessage
	 * @param mapParams
	 */
	public void setParam(AbstractTemplateField templateField, InMessage inMessage, Map<String, List<String>> Params);
	
}
