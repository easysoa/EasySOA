/**
 * 
 */
package org.easysoa.template;

import java.util.HashMap;

import org.apache.log4j.Logger;
import org.easysoa.records.ExchangeRecord;

/**
 * @author jguillemotte
 *
 */
public class TemplateRenderer {

	// Logger
	private static Logger logger = Logger.getLogger(TemplateRenderer.class.getName());	
	
	// Structure to store field values
	private HashMap<String, String> fieldMap;
	
	/**
	 * Default constructor
	 */
	public TemplateRenderer(){
		fieldMap = new HashMap<String, String>();
	}
	
	/**
	 * Render the template to an exploitable HTML form
	 * @param recordTemplate
	 */
	public void render(ExchangeRecord exchangeRecord){
		/**
		The TemplateRenderer executes a record (request) template by loading its record and rendering 
		it in the chosen template engine, with template variables set to user provided values first, 
		doing the corresponding request call and returning its result. Note that the TemplateRenderer 
		can therefore be used (or tested) with hand-defined exchange (request) templates.
		*/
		// Load the custom exchangeRecord
		// for each custom parameter (eg : starting with $ for velocity)
		// Get the default value and give it to the template using an hashmap
		
		// Fill in the field hashmap
		
		
		
		
		// Generate here the template for Velocity for instance
		
	}
	
	/**
	 * Method to be used in the template for suggested fields
	 * The Template builder have to use this syntax : $renderer.getFieldValue("fieldName")
	 * @param fieldName
	 * @return
	 */
	public String getFieldValue(String fieldName){
		return fieldMap.get(fieldName);
	}
	
}
