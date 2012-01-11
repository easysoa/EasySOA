/**
 * 
 */
package org.easysoa.template;

import java.util.List;

import org.apache.log4j.Logger;
import org.easysoa.records.ExchangeRecord;
import org.easysoa.records.ExchangeRecordStoreManager;
import org.easysoa.records.persistence.filesystem.ExchangeRecordFileStore;

import com.openwide.easysoa.message.InMessage;

/**
 * @author jguillemotte
 *
 */
public class TemplateBuilder {
	
	// Logger
	private static Logger logger = Logger.getLogger(TemplateBuilder.class.getName());	
	
	/**
	 * Default constructor
	 */
	public TemplateBuilder(){
		
	}
	
	/**
	 * Take as parameter to work templateFieldSuggestion and request
	 * Then returns a template ready to send to the template renderer	
	 * @param fieldSuggestions
	 * @param inMessage
	 * @return 
	 */
	public ExchangeRecord buildTemplate(TemplateFieldSuggestions fieldSuggestions, ExchangeRecord record) throws IllegalArgumentException {

		// What to do if the suggested fields are not found in the reciord InMessage ?? throws an exception, do nothing ?
		/**
		The TemplateBuilder applies a selection of TemplateFieldSuggestions on a record (request) 
		and uses their fieldSetterInfos to replace those fields' values by template variables (ex. in velocity : $myFieldName), 
		and saves the result in a different record. Note that the TemplateBuilder can therefore be used (or tested)
	 	with hand-defined TemplateFieldSuggestions.
		**/
		if(fieldSuggestions == null || record == null){
			throw new IllegalArgumentException("Parameters fieldSuggestions and inMessage must not be null !");
		}
		for(TemplateField field : fieldSuggestions){
			logger.debug("Field to replace in a new custom record : " + field.getFieldName() + " = " + field.getDefaultValue());
			// Make a copy of exchange record, save it or pass it directly to templateRenderer ?
			// In case of saving the custom record on disk, where to save it ?
			// Replace in the cloned record the suggested files by a template variable => eg : for velocity $bean.variableName

			// Generate a velocity template and save it in the disk
			
			// How to retrieve where the suggested field is stored in the record exchange ? Path, query or form param ?
			// No information about this is available in the correlationField ...
			record.getInMessage();
			
			ExchangeRecordFileStore fileStore= new ExchangeRecordFileStore();
			fileStore.setStorePath("templateTest/");
			try {
				//fileStore.save(record);
			}
			catch(Exception ex){
				logger.error("Unable to save the custom record", ex);
			}
			
		}
		// 
		return null;
	}
	
}
