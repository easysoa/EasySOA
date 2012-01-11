/**
 * 
 */
package org.easysoa.template;

import java.util.HashMap;
import java.util.Iterator;
import java.util.StringTokenizer;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JSONSerializer;

import org.apache.log4j.Logger;
import org.easysoa.records.ExchangeRecord;
import org.easysoa.records.ExchangeRecordStore;
import org.easysoa.records.correlation.CandidateField;
import org.easysoa.records.correlation.CorrelationService;
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
		
	}

	/**
	 * Suggest fields to use in a template from an exchange record
	 * @return 
	 */
	// TODO :  Establish correlations whit only one record ??? seem's to not work very well
	public TemplateFieldSuggestions suggest(ExchangeRecord record){
		// from record, Get the inMessage
		// try to find all the fields (headers, form, ...)
		// Use the correlation system to detect fields suggest

		// make 4 hashmaps (for query param fields, for content param fields, for pat param fields and a last for response fields)
		// then call the correlate method with the record and the four hashmaps to get the suggested fields
		
		// So to fill the hashmaps, need to create one or several methods to get specific params from InMessage
		
		// It is necessary to do some refactoring of correlation package to re-organize methods and classes
		// eg : these methods will be at better place in a correlation class 

		// seem's that do not work when no outputfields are set in the hashmap
		CorrelationService correlationService = new CorrelationService();
		return correlationService.correlateWithSubpath(record,
				getPathParams(record.getInMessage()),
				getQueryParams(record.getInMessage()),
				getContentParam(record.getInMessage()),
				getOutFields(record.getOutMessage()));
		//
		/*correlationService.correlate(record,
				getPathParams(record.getInMessage()),
				getQueryParams(record.getInMessage()),
				getContentParam(record.getInMessage()),
				getOutFields(record.getOutMessage()));
		*/		
		//
	}
	
	/**
	 * Suggest fields to use in a template from a whole exchange record store 
	 * @param recordStore
	 * @return
	 */
	public TemplateFieldSuggestions suggest(ExchangeRecordStore recordStore){
		// from record store, for each record
		for(ExchangeRecord record : recordStore){
			suggest(record);
		}
		return null;
	}
	
	/**
	 * 
	 * @param outMessage
	 * @return
	 */
	private HashMap<String, CandidateField> getOutFields(OutMessage outMessage){
		HashMap<String,CandidateField> fieldMap = new HashMap<String,CandidateField>();
		// TODO add code here to fill hashmap with output fields
		// Message content can be very different (json, xml ...)
		// TODO Add a system to choose the parser corresponding to the out message content
		logger.debug("outMessage " + outMessage.getMessageContent().getContent());
		try{
			JSONObject jsonOutObject = (JSONObject) JSONSerializer.toJSON(outMessage.getMessageContent().getContent());
			findJSONOutFields("root", jsonOutObject, -1, fieldMap);
		}
		catch(Exception ex){
			logger.warn("Response is not a JSON string, trying another parser to find output fields");
		}
		logger.debug("Out param fields map : " + fieldMap);		
		return fieldMap;
	}
	
	/**
	 * Recursive method to fill the OutFieldMap from JSON Datastructure
	 * @param objectKey
	 * @param jsonObject
	 * @param index
	 * @param fieldMap
	 */
	private void findJSONOutFields(String objectKey, Object jsonObject, int index, HashMap<String,CandidateField> fieldMap){
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
	}
	
	/**
	 * 
	 * @param inMessage
	 * @return
	 */
	private HashMap<String, CandidateField> getPathParams(InMessage inMessage){
		HashMap<String,CandidateField> fieldMap = new HashMap<String,CandidateField>();
		CandidateField field;
		StringTokenizer tokenizer = new StringTokenizer(inMessage.getPath(), "/");
		int pathPos = 0;
		while(tokenizer.hasMoreTokens()){
			String token = tokenizer.nextToken();
			field = new CandidateField("path." + pathPos, token);
			fieldMap.put("path." + pathPos, field);
			pathPos++;
		}
		logger.debug("Path param fields map : " + fieldMap);
		return fieldMap;
	}
	
	/**
	 * Take the query params from <code>InMessage</code> and fill an HashMap with them. 
	 * @param inMessage <code>InMessage</code> containing query params
	 * @return An <code>HashMap</code> filled with query parameters 
	 */
	private HashMap<String, CandidateField> getQueryParams(InMessage inMessage){
		HashMap<String,CandidateField> fieldMap = new HashMap<String,CandidateField>();
		CandidateField candidateField;
		for(QueryParam queryParam : inMessage.getQueryString().getQueryParams()){
			candidateField = new CandidateField(queryParam.getName(), queryParam.getValue());
			fieldMap.put(candidateField.getPath(), candidateField);
		}
		logger.debug("Query param fields map : " + fieldMap);		
		return fieldMap;
	}
	
	/**
	 * Take the content params (eg : HTML form POST params) from <code>InMessage</code> and fill an HashMap with them. 
	 * @param inMessage <code>InMessage</code> containing query params
	 * @return An <code>HashMap</code> filled with query parameters
	 */
	private HashMap<String, CandidateField> getContentParam(InMessage inMessage){
		HashMap<String,CandidateField> fieldMap = new HashMap<String,CandidateField>();
		CandidateField candidateField;
		StringTokenizer tokenizer = new StringTokenizer(inMessage.getMessageContent().getContent(), "&");
		String token;
		while(tokenizer.hasMoreTokens()){
			token = tokenizer.nextToken();
			candidateField = new CandidateField(token.substring(0, token.indexOf("=")), token.substring(token.indexOf("=")));
			fieldMap.put(candidateField.getPath(), candidateField);
		}
		logger.debug("Content param fields map : " + fieldMap);
		return fieldMap;
	}
}
