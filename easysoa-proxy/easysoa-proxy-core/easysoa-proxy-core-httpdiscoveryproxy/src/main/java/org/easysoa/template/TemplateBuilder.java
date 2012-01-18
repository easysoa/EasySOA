/**
 * 
 */
package org.easysoa.template;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import org.apache.log4j.Logger;
import org.easysoa.records.ExchangeRecord;
import org.easysoa.records.persistence.filesystem.ExchangeRecordFileStore;
import org.easysoa.template.TemplateField.TemplateFieldType;
import com.openwide.easysoa.message.QueryParam;

/**
 * @author jguillemotte
 *
 */
public class TemplateBuilder {
	
	// Logger
	private static Logger logger = Logger.getLogger(TemplateBuilder.class.getName());	
	
	// Template expressions segments
	//private final static String VARIABLE_BEAN_PREFIX = "$renderer.getFieldValue(\"";
	private final static String VARIABLE_BEAN_PREFIX = "$arg1.get(\"";
	private final static String VARIABLE_BEAN_SUFFIX = "\")";
	
	/**
	 * Default constructor
	 */
	public TemplateBuilder(){
		
	}
	
	/**
	 * Take as parameter to work templateFieldSuggestion and request
	 * Then returns a template (or a custom exchange record ???) ready to send to the template renderer
	 * This method produces 2 files : a custom exchangeRecord AND a template to use with
	 * @param fieldSuggestions
	 * @param inMessage
	 * @return 
	 * @throws Exception 
	 */
	public Map<String, String> buildTemplate(TemplateFieldSuggestions fieldSuggestions, ExchangeRecord record) throws Exception {
		// What to do if the suggested fields are not found in the record InMessage ?? throws an exception, do nothing ?
		/**
		The TemplateBuilder applies a selection of TemplateFieldSuggestions on a record (request) 
		and uses their fieldSetterInfos to replace those fields' values by template variables (ex. in velocity : $myFieldName), 
		and saves the result in a different record. Note that the TemplateBuilder can therefore be used (or tested)
	 	with hand-defined TemplateFieldSuggestions.
		**/
		if(fieldSuggestions == null || record == null){
			throw new IllegalArgumentException("Parameters fieldSuggestions and originalRecord must not be null !");
		}
		Map<String, String> templateFileMap = new HashMap<String, String>();
		for(TemplateField field : fieldSuggestions.getTemplateFields()){
			logger.debug("Field to replace in a new custom record : " + field.getFieldName() + " = " + field.getDefaultValue());
			logger.debug("Field type : " + field.getParamType() + ", position in path : " + field.getPathParamPosition());

			// Make a copy of exchange record, save it or pass it directly to templateRenderer ?
			// In case of saving the custom record on disk, where to save it ?
			// Replace in the cloned record the suggested files by a template variable => eg : for velocity $bean.variableName
			// Syntax to use for template variable : $renderer.getFieldValue("fieldName")
			// See TemplateRenderer.getFieldValue method
			
			// Generate a velocity template and save it in the disk
			// In this template we must have : 
			// - A reference on the TemplateRendererBean (provide suggested field values)
			// - No presentation or display informations, must be generic
			
			// How to retrieve where the suggested field is stored in the record exchange ? Path, query or form param ?
			// No information about this is available in the correlationField ... Need to use templateField like bean with data concerning the param type (and position)
			
			// Replace the parameter values with template expressions
			if(TemplateFieldType.QUERY_PARAM.equals(field.getParamType())){
				// TODO : Can be a problem in case of the same value appears several time in the query, for instance check boxes ...
				List<QueryParam> paramList = record.getInMessage().getQueryString().getQueryParams();
				for(QueryParam param : paramList){
					if(param.getName().equals(field.getFieldName())){
						param.setValue(VARIABLE_BEAN_PREFIX + field.getFieldName() + VARIABLE_BEAN_SUFFIX);
						break;
					}
				}
			} else if(TemplateFieldType.PATH_PARAM.equals(field.getParamType())){
				String path = record.getInMessage().getPath();
				// Fastest solution is to replace the value corresponding to the field
				// TODO : Can be a problem in case of the same value appears several time in the path ...
				record.getInMessage().setPath(path.replace(field.getDefaultValue(), VARIABLE_BEAN_PREFIX + field.getFieldName() + VARIABLE_BEAN_SUFFIX)); // TODO argMap.get(...)
				// Other solution with a StringTokenizer, need to add a StringBuffer to appends tokens to be complete
				/*StringTokenizer tokenizer = new StringTokenizer(path, "/");
				int tokenPosition = 0;
				while(tokenizer.hasMoreTokens()){
					String token = tokenizer.nextToken();
					// Compare here the position OR the value ??
					if(tokenPosition == field.getPathParamPosition()){
						token = VARIABLE_BEAN_PREFIX + field.getFieldName() + VARIABLE_BEAN_SUFFIX;
					}
					tokenPosition++;
				}*/
			} else if(TemplateFieldType.CONTENT_PARAM.equals(field.getParamType())){
				// TODO : To implements
			} else if(TemplateFieldType.WSDL_PARAM.equals(field.getParamType())){
				// TODO : To implements
			} else {
				logger.debug("Unable to replace value for unknow field type '" + field.getParamType() + "'");
			}
		}
		if(fieldSuggestions != null && fieldSuggestions.getTemplateFields().size() > 0){
			// Store the custom exchange record
			ExchangeRecordFileStore fileStore= new ExchangeRecordFileStore();
			// TODO : This path must be configurable
			// TODO : How to configure Velocity to use a path other that the one configured in the composite file
			// The composite configured path is in target/classes ...
			fileStore.setStorePath("target/classes/webContent/templates/");
			try {
				templateFileMap = fileStore.save(new TemplateRecord(record));
				// Add code to save a fld : containing field suggestions with default values
				fileStore.save(fieldSuggestions, record.getExchange().getExchangeID());
			}
			catch(Exception ex){
				logger.error("Unable to save the templates", ex);
				throw ex;
			}
			return templateFileMap;			
		}
		// Returns null if there is not suggested fields
		return null;
	}
	
}
