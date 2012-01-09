/**
 * 
 */
package org.easysoa.template;

/**
 * SOAP Template service :
 * Build a WSDL. Each operation exposed in the WSDL corresponds to a fld template
 * The goal is to obtain a WSDL that can work with the scaffolder proxy 
 * 
 * @author jguillemotte
 *
 */
public interface SOAPTemplateService {

	public String generateWSDLServiceTemplate();
	
}
