/**
 * 
 */
package org.easysoa.template;

import java.util.List;
import org.apache.log4j.Logger;
import org.easysoa.records.ExchangeRecord;
import org.easysoa.template.TemplateField.TemplateFieldType;
import com.openwide.easysoa.message.QueryParam;

/**
 * Generate and save request and response templates made from field suggestions, an exchange record and an Exchange store name *
 *  
 * @author jguillemotte
 *
 */
public class TemplateBuilder {
	
	// Logger
	private static Logger logger = Logger.getLogger(TemplateBuilder.class.getName());	
	
	// Template expressions segments
	private final static String VARIABLE_BEAN_PREFIX = "$arg2.get(\"";
	// TODO Get the first param value of the list, how to get multiple values for the same param ?
	private final static String VARIABLE_BEAN_SUFFIX = "\").get(0)";
	
	/**
	 * Default constructor
	 */
	public TemplateBuilder(){
        /*this.paramSetterList.add(new RestFormParamSetter());
        this.paramSetterList.add(new RestPathParamSetter());
        this.paramSetterList.add(new RestQueryParamSetter());
        this.paramSetterList.add(new WSDLParamSetter());*/ 		
	}
	
	/**
	 * Generate templates for request and response part of an exchange record
	 * store in the template store : a customized record AND a velocity macro template 
	 * @param fieldSuggestions Fields to replace by template expressions (Velocity expression in this case)
	 * @param record The exchange record to templatize
	 * @param runName The store name to save the template 
	 * @return a templatized record
	 * @throws Exception If a problem occurs
	 */
	public ExchangeRecord templatizeRecord(TemplateFieldSuggestions fieldSuggestions, ExchangeRecord record/*, String runName*/) throws Exception {
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
		//Map<String, String> templateFileMap = new HashMap<String, String>();
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
		
		
		/*if(fieldSuggestions != null && fieldSuggestions.getTemplateFields().size() > 0){
			// Store the custom exchange record
			//ProxyExchangeRecordFileStore fileStore= new ProxyExchangeRecordFileStore();
			//fileStore.setStorePath(PropertyManager.getProperty("path.template.store"));
			try {
				// TODO : regroup save operations in a same method in StoreManager
				// Make a copy of the original record
				//fileStore.createStore(runName);
				//fileStore.setStorePath(PropertyManager.getProperty("path.template.store") + runName + "/");
			    // Save customized record
				//fileStore.save(record);
				// templateFileMap = fileStore.saveTemplate(new TemplateRecord(record), runName);
				// Add code to save a fld : containing field suggestions with default values
				//fileStore.saveFieldSuggestions(fieldSuggestions, runName, record.getExchange().getExchangeID());
			}
			catch(Exception ex){
				logger.error("Unable to save the templates", ex);
				throw ex;
			}
			return templateFileMap;			
		}*/
		// Returns null if there is not suggested fields
		return record;
	}

	/**
	 * Build Velocity template from the templatized record
	 * @param templatizedRecord templatized record
	 * @return A velocity template
	 */
	public VelocityTemplate buildTemplate(ExchangeRecord templatizedRecord){
	    return new VelocityTemplate(templatizedRecord);
	}
	
}
