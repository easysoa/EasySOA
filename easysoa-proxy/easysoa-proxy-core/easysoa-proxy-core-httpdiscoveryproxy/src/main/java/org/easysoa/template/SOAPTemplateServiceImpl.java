package org.easysoa.template;

/**
 * 
 * @author jguillemotte
 *
 */
public class SOAPTemplateServiceImpl implements SOAPTemplateService {

	// TODO : How to generate a custom WSDL file (generate here a real SOAP service, not only a single WSDL file !)

	// TODO : How the actual template system works with WSDL
	// Make a test for this case
	
	// pb ? 
	// - from an exchange record (rest or soap) => generate a WSDL.
	// - call the scaffolder proxy with the previously generated WSDL to obtain an htlm form with default field values
	// - send a request to execute an operation corresponding to a FLD template manually defined
	// - 
	
	@Override
	public String generateWSDLServiceTemplate() {
		// returns a generated WSDL from fld template collection
		return null;
	}

}
