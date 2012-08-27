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
package org.easysoa.proxy.core.api.template.setters;

import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

/**
 * @author jguillemotte
 *
 */
@Deprecated
public class ParamTokenizer {

	/**
	 * Take a parameter string (eg : param1=value1&param2=value2&param3=value3 ...) and replace the values of parameters corresponding to the fieldName
	 * @param fieldName Field name 
	 * @param paramString Parameter <code>String</code>
	 * @param mapParams A <code>MultivaluedMap</code> containing one or several values for the fieldName
	 * @return A parameter <code>String</code> with values replaced for the field name parameter
	 */
	public final static String replaceParamValues(String fieldName, String paramString, Map<String, List<String>> params){
		StringBuffer buffer = new StringBuffer();
		StringTokenizer tokenizer = new StringTokenizer(paramString, "&");
		String newValue;
		int index = 0;
		while(tokenizer.hasMoreElements()){
			String token = tokenizer.nextToken();
			if(buffer.length() > 0){
				buffer.append("&");
			}
			if(token.startsWith(fieldName)){
				newValue = token.substring(0, token.lastIndexOf("=")) + params.get(fieldName).get(index);
				index++;
				buffer.append(newValue);
			} else {
				buffer.append(token);
			}
		}
		return buffer.toString();
	}
	
}
