/**
 * 
 */
package org.easysoa.template;

import org.apache.log4j.Logger;
import org.easysoa.records.ExchangeRecord;

/**
 * @author jguillemotte
 *
 */
public class TemplateRenderer {

	// Logger
	private static Logger logger = Logger.getLogger(TemplateRenderer.class.getName());	
	
	/**
	 * Default constructor
	 */
	public TemplateRenderer(){
		
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
		// Get the default value and set give it to the template ...
		
	}
	
}
