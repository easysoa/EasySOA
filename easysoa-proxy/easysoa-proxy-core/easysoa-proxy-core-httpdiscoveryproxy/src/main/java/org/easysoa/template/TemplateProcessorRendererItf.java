/**
 * 
 */
package org.easysoa.template;

/**
 * @author jguillemotte
 *
 */
public interface TemplateProcessorRendererItf {

	public String renderRes(String templatePath);
	
	public String renderReq(String templatePath);
	
}
