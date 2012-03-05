/**
 * 
 */
package org.easysoa.template.setters;

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
