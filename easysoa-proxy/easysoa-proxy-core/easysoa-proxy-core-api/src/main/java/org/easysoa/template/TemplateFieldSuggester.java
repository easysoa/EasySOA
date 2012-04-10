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
package org.easysoa.template;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;

import org.apache.log4j.Logger;
import org.easysoa.records.ExchangeRecord;
import org.easysoa.records.correlation.CandidateField;
import org.easysoa.records.correlation.CorrelationEngineImpl;
import org.easysoa.records.correlation.FieldExtractor;
import org.easysoa.template.parsers.JSONParser;
import org.easysoa.template.parsers.TemplateParser;
import org.easysoa.template.parsers.XMLParser;

import com.openwide.easysoa.message.InMessage;
import com.openwide.easysoa.message.OutMessage;
import com.openwide.easysoa.message.QueryParam;

/**
 * This class suggest fields to use from 
 * A TemplateFieldSuggester works on anything (a request or record, a whole record store, a service definition...) 
 * to suggest fields that would be interesting to make template variables of.
 * Therefore its output is similar to the one of the first TemplateService above (see template.fld file).
 * 
 * @author jguillemotte
 *
 */
public class TemplateFieldSuggester {

	// Logger
	private static Logger logger = Logger.getLogger(TemplateFieldSuggester.class.getName());
	
	/**
	 * Default constructor
	 */
	public TemplateFieldSuggester(){
		// Nothing to do here
	}

	/**
	 * Suggest fields to use in a template from an exchange record
	 * @return 
	 */
	// TODO :  Establish correlations with only one record ??? seem's to not work very well
	public TemplateFieldSuggestions suggest(ExchangeRecord record){
		// from record, Get the inMessage
		// try to find all the fields (headers, form, ...)
		// Use the correlation system to detect fields suggest

		// make 4 hashmaps (for query param fields, for content param fields, for pat param fields and a last for response fields)
		// then call the correlate method with the record and the four hashmaps to get the suggested fields
		
		// So to fill the hashmaps, need to create one or several methods to get specific params from InMessage
		
		// It is necessary to do some refactoring of correlation package to re-organize methods and classes
		// eg : these methods will be at better place in a correlation class 

		// this correlation service return nothing when no outputfields are set in the hashmap
		// Or when there is no corresponding fields between in parameters and out parameters
	    // => in this case, here we could return all input fields (as a worse case)
	    FieldExtractor extractor = new FieldExtractor();
		CorrelationEngineImpl correlationService = new CorrelationEngineImpl();
		return correlationService.correlateWithSubpath(record,
				extractor.getInputPathParams(record.getInMessage()),
				extractor.getInputQueryParams(record.getInMessage()),
				extractor.getInputContentParam(record.getInMessage()),
				extractor.getOutputFields(record.getOutMessage()));

		/*correlationService.correlate(record,
				getPathParams(record.getInMessage()),
				getQueryParams(record.getInMessage()),
				getContentParam(record.getInMessage()),
				getOutFields(record.getOutMessage()));
		*/		
	}
	
	/**
	 * Recursive method to fill the OutFieldMap from JSON Datastructure
	 * @param objectKey
	 * @param jsonObject
	 * @param index
	 * @param fieldMap
	 */
	/*private void findJSONOutFields(String objectKey, Object jsonObject, int index, HashMap<String,CandidateField> fieldMap){
		if(jsonObject instanceof JSONObject){
			// Get all the key contained in the object and for each key, get the associated object and call this recursive method
			logger.debug("Instance of JSONObject found");
			JSONObject jObject = (JSONObject) jsonObject;
			@SuppressWarnings("unchecked")
			Iterator<String> keyIterator = jObject.keys();
			while(keyIterator.hasNext()){
				String key = (String)(keyIterator.next());
				logger.debug("key : " + key);
				findJSONOutFields(key, jObject.get(key), index, fieldMap);
			}
		} else if(jsonObject instanceof JSONArray) {
			// For each JSONObject contained in the array, call this recursive method
			logger.debug("Instance of JSONArray found");
			JSONArray jsonArray = (JSONArray) jsonObject;
			int objectIndex = 0;
			for(Object object : jsonArray){
				findJSONOutFields(objectKey, object, objectIndex, fieldMap);
				objectIndex++;
			}
		} else {
			// Get the value and add a new CandidateField object in the map
			logger.debug("Other object found (" + jsonObject.getClass().getName() + ") = " + objectKey + ":" + jsonObject.toString());			
			String fieldName;
			if(index > -1){
				fieldName = index + "/" + objectKey;
			} else {
				fieldName = objectKey;
			}
			CandidateField field = new CandidateField(fieldName, jsonObject.toString());
			fieldMap.put(fieldName, field);
			return;
		}
	}*/
	

}
