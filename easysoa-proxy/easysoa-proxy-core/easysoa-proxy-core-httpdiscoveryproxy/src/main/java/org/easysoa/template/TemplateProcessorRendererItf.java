/**
 * 
 */
package org.easysoa.template;

import java.util.Map;

import org.easysoa.records.ExchangeRecord;

/**
 * @author jguillemotte
 *
 */
public interface TemplateProcessorRendererItf {

	public String renderReq(String templatePath, ExchangeRecord record, Map<String, String> fieldValues) throws Exception;
	
	public String renderRes(String templatePath, ExchangeRecord record, Map<String, String> fieldValues) throws Exception;
}
