/**
 * 
 */
package org.easysoa.template;

import java.util.List;

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
	public String renderReq(String path, List<Object> list);
	
	/**
	 * Render the response
	 * @return
	 */
	public String renderRes(String path);
	
}
