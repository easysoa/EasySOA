/**
 * 
 */
package org.easysoa.template;

import java.util.Map;
import org.apache.log4j.Logger;
import org.easysoa.records.ExchangeRecord;
import org.osoa.sca.annotations.Reference;

/**
 * @author jguillemotte
 *
 */
public class TemplateRenderer implements TemplateProcessorRendererItf {

	// Logger
	private static Logger logger = Logger.getLogger(TemplateRenderer.class.getName());	
	
	// Reference to template object provided by FraSCAti
	@Reference(required = true)
    protected TemplateRendererItf template;
	
	/**
	 * Default constructor
	 */
	public TemplateRenderer(){
	}
	
	@Override
	public String renderReq(String templatePath, ExchangeRecord record, String runName, Map<String, String> fieldValues) throws Exception {
		/**
		The TemplateRenderer executes a record (request) template by loading its record and rendering 
		it in the chosen template engine, with template variables set to user provided values first, 
		doing the corresponding request call and returning its result. Note that the TemplateRenderer 
		can therefore be used (or tested) with hand-defined exchange (request) templates.
		*/
		// Render the template
		String renderedTemplate = template.renderReq(templatePath, runName, fieldValues);
		logger.debug("Rendered template : " + renderedTemplate);
		
		// Execute the template
		TemplateExecutor executor = new TemplateExecutor();
		return executor.execute(renderedTemplate);
	}

	@Override
	public String renderRes(String templatePath, ExchangeRecord record, String runName, Map<String, String> fieldValues){
		// TODO : Complete this method, to be used in a server mock
		logger.warn("renderRes method not yet entierely implemented, need to be completed !");
		String renderedTemplate = template.renderRes(templatePath, runName, fieldValues);
		return renderedTemplate;
	}
	
}
