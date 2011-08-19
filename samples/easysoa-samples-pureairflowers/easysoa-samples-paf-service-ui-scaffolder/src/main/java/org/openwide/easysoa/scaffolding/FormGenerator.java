package org.openwide.easysoa.scaffolding;

public interface FormGenerator {

	/**
	 * 
	 * @param xml XML or WSDL Source file name
	 * @param xsl XSLT Source file name
	 * @param html Output HTML file name
	 * @throws Exception
	 */
	public String generateHtmlFormFromWsdl(String xml, String xsl, String html) /*throws Exception*/;

}