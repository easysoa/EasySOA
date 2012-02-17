/**
 * 
 */
package org.easysoa.template;

import java.util.List;
import java.util.Map;

import javax.ws.rs.core.MultivaluedMap;

import org.apache.log4j.Logger;
import org.easysoa.records.ExchangeRecord;
import org.osoa.sca.annotations.Reference;

import com.openwide.easysoa.message.OutMessage;

/**
 * Render a template (replace the expressions by provided values)
 * 
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
	
    /* (non-Javadoc)
     * @see org.easysoa.template.TemplateProcessorRendererItf#renderReq()
     */	
	@Override
	public OutMessage renderReq(String templatePath, ExchangeRecord record, String runName, Map<String, List<String>> fieldValues) throws Exception {
		/**
		The TemplateRenderer executes a record (request) template by loading its record and rendering 
		it in the chosen template engine, with template variables set to user provided values first, 
		doing the corresponding request call and returning its result. Note that the TemplateRenderer 
		can therefore be used (or tested) with hand-defined exchange (request) templates.
		*/
		// Render the template
		String renderedTemplate = template.renderReq(templatePath, runName, fieldValues);
		// Execute the template
		TemplateExecutor executor = new TemplateExecutor();
		// Return only the message content
		// TODO : Maybe good idea to return the entire response as JSONObject or other format ...
		return executor.execute(renderedTemplate);
	}

    /* (non-Javadoc)
     * @see org.easysoa.template.TemplateProcessorRendererItf#renderRes()
     */ 
	@Override
	public String renderRes(String templatePath, ExchangeRecord record, String runName, Map<String, List<String>> fieldValues) throws Exception {
		// TODO : Complete this method, to be used in a server mock
		logger.warn("renderRes method not yet entierely implemented, need to be completed !");
		String renderedTemplate = template.renderRes(templatePath, runName, fieldValues);

		return renderedTemplate;
	}
	
}
