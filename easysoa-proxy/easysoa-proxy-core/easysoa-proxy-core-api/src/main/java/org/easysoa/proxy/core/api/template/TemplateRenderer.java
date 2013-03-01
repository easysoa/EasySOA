/**
 * EasySOA Proxy Copyright 2011 Open Wide
 *
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or (at your option) any
 * later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 *
 * Contact : easysoa-dev@googlegroups.com
 */
package org.easysoa.proxy.core.api.template;

import java.util.List;
import java.util.Map;
import org.apache.log4j.Logger;
import org.easysoa.message.OutMessage;
import org.easysoa.records.ExchangeRecord;
import org.osoa.sca.annotations.Reference;
import org.osoa.sca.annotations.Scope;

/**
 * Render a template (replace the expressions by provided values)
 *
 * @author jguillemotte
 *
 */
@Scope("composite")
public class TemplateRenderer implements TemplateProcessorRendererItf {

    // Logger
    private static Logger logger = Logger.getLogger(TemplateRenderer.class.getName());

    // Reference to template object provided by FraSCAti
    @Reference(required = true)
    protected GenericTemplateRendererItf template;

    /**
     * Default constructor
     */
    public TemplateRenderer() {
    }

    /* (non-Javadoc)
     * @see org.easysoa.template.TemplateProcessorRendererItf#renderReq()
     */
    @Override
    public OutMessage renderReq(String templatePath, ExchangeRecord record, String runName, Map<String, List<String>> fieldValues) throws Exception {
        /**
         * The TemplateRenderer executes a record (request) template by loading
         * its record and rendering it in the chosen template engine, with
         * template variables set to user provided values first, doing the
         * corresponding request call and returning its result. Note that the
         * TemplateRenderer can therefore be used (or tested) with hand-defined
         * exchange (request) templates.
         */
        // Render the template
        String renderedTemplate = template.execute_custom(templatePath, runName, fieldValues, null);
        
        logger.debug("Rendered template : " + renderedTemplate);
        // Execute the template
        TemplateExecutor executor = new RequestTemplateExecutor();
        // Return only the message content
        // TODO : Maybe good idea to return the entire response as JSONObject or other format ...
        return executor.execute(renderedTemplate);
    }

    /* (non-Javadoc)
     * @see org.easysoa.template.TemplateProcessorRendererItf#renderRes()
     */
    @Override
    public OutMessage renderRes(String templatePath, ExchangeRecord record, String runName, Map<String, List<String>> fieldValues) throws Exception {
        // TODO : Complete this method, to be used in a server mock
        logger.warn("renderRes method not yet entierely implemented, need to be completed !");
        
        // Problem : in this case the method renderRes is passed as "method" parameter in the proxy hack, how to invoke a method with a dynamic name ????
        // using reflection ??? Byte code modification ??
        
        // Solution : use a reserved method name (eg : "execute_custom") (and a generic templateRendererInterface) to call the special code in the proxy velocity
        // The parameters to use will be set in the the parameters passed in the method
        
        //String renderedTemplate = template.renderRes(templatePath, runName, fieldValues);
        String renderedTemplate = template.execute_custom(templatePath, runName, fieldValues, null);

        logger.debug("Rendered template : " + renderedTemplate);
        // TODO a template a template executor for response => do not call the request forwarder
        TemplateExecutor executor = new ResponseTemplateExecutor();
        return executor.execute(renderedTemplate);
    }
}

