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
package org.easysoa.proxy.core.api.template;

import java.util.List;
import java.util.Map;

import org.easysoa.message.OutMessage;
import org.easysoa.records.ExchangeRecord;

/**
 * @author jguillemotte
 *
 */
public interface TemplateProcessorRendererItf {

	/**
	 * Render the request template by replacing template expression by provided values
	 * @param templatePath The request template
	 * @param record The associated record
	 * @param fieldValues Provided field values
	 * @return The rendered template
	 * @throws Exception If a problem occurs
	 */
	public OutMessage renderReq(String templatePath, ExchangeRecord record, String runName, Map<String, List<String>> fieldValues) throws Exception;
	
	/**
	 * Render the response template by replacing template expression by provided values 
	 * @param templatePath The response template
	 * @param record The associated record
	 * @param fieldValues Provided field values
	 * @return The rendred template
	 * @throws Exception If a problem occurs
	 */
	public OutMessage renderRes(String templatePath, ExchangeRecord record, String runName, Map<String, List<String>> fieldValues) throws Exception;
}
