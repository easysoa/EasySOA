/**
 * 
 */
package org.easysoa.template;

import java.util.Map;

/**
 * @author jguillemotte
 *
 */
public interface TemplateRendererItf {

	/**
	 * Render the request
	 * @param list 
	 * @return
	 */
	public String renderReq(String path, String runName, Map<String, String> argMap);

	/**
	 * Render the response
	 * @return
	 */
	public String renderRes(String path, String runName, Map<String, String> argMap);
	
}
