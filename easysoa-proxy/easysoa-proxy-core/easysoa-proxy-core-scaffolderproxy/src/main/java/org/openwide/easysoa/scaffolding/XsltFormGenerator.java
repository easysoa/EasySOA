/**
 * EasySOA Proxy
 * Copyright 2011 Open Wide
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * 
 * Contact : easysoa-dev@googlegroups.com
 */

package org.openwide.easysoa.scaffolding;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.net.URL;

import javax.xml.transform.Result;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.apache.log4j.Logger;
import org.easysoa.proxy.common.ProxyUtil;
import org.osoa.sca.annotations.Property;
import org.xml.sax.InputSource;

/**
 * Transform a WSDL into an HTML form with an XSLT transformation
 * 
 * @author jguillemotte
 *
 */
public class XsltFormGenerator implements TransformationFormGeneratorInterface {

	/**
	 * Logger
	 */
	private static Logger logger = Logger.getLogger(XsltFormGenerator.class.getClass());	

	@Property
	String defaultWsdl;
		
	/* (non-Javadoc)
	 * @see org.openwide.easysoa.scaffolding.FormGenerator#generateHtmlFormFromWsdl(java.lang.String, java.lang.String, java.lang.String)
	 */
	//@Override
	public String generateHtmlFormFromWsdl(String wsdlXmlSource, String formWsdlXmlSource, String xsltSource, String htmlOutput) {
		// DOM is old, need to add a call to setNamespacesAware(true) to avoid a problem of unrecognized namespace
		// Use SAX instead
		/*
		DocumentBuilderFactory fabriqueD = DocumentBuilderFactory.newInstance();
		fabriqueD.setNamespaceAware(true);
		DocumentBuilder constructeur = fabriqueD.newDocumentBuilder();
		File fileXml = new File(xml);
		Document document = constructeur.parse(fileXml);
		Source source = new DOMSource(document);
		*/
		logger.debug("wsdlXmlSource : " + wsdlXmlSource);
		logger.debug("formWsdlXmlSource : " + formWsdlXmlSource);
		logger.debug("xsltSource : " + xsltSource);
		logger.debug("htmlOutput : " + htmlOutput);
		logger.debug("defautWsdl : " + defaultWsdl);
		try{
			// Deactivated for the Talend tuto Hack !!
			/*
			if(xmlSource == null || "".equals(xmlSource)){
				throw new IllegalArgumentException("The parameter xmlSource cannot be null or empty !");
			}
			else*/
			if(formWsdlXmlSource == null || "".equals(formWsdlXmlSource)){
				formWsdlXmlSource = wsdlXmlSource;
			}
			if(xsltSource == null || "".equals(xsltSource)){
				throw new IllegalArgumentException("The parameter xsltSource cannot be null or empty !");
			}
			else if(htmlOutput == null || "".equals(htmlOutput)){
				throw new IllegalArgumentException("The parameter html cannot be null or empty !");
			}
			
			// Hack for Talend airport sample
			if(formWsdlXmlSource == null || "".equals(formWsdlXmlSource)){
				formWsdlXmlSource = defaultWsdl;
			}
			URL formWsdlXmlUrl = ProxyUtil.getUrlOrFile(formWsdlXmlSource);
			// Parsing XML
			// Can works with HTTP protocol (http://...) or FILE protocol (file://...)
			SAXSource source = new SAXSource(new InputSource(new InputStreamReader(formWsdlXmlUrl.openStream())));
			
			// Output HTML file
			File htmlOutputFile = new File(htmlOutput);
			Result result = new StreamResult(htmlOutputFile);
			// Transformer configuration
			TransformerFactory tFactory = TransformerFactory.newInstance();
			StreamSource xsltSourceStream = new StreamSource(new File(xsltSource));
			Transformer transformer = tFactory.newTransformer(xsltSourceStream);
			// set transfo context params in engine
			transformer.setParameter("wsdlUrl", wsdlXmlSource);
			// Transformation
			transformer.transform(source, result);
			// Return the form
			return readResultFile(htmlOutputFile);
		}
		catch(Exception ex){
			String msg = "Transformation failed";
			logger.error(msg, ex);
			return msg + " : " + ex.getMessage();
		}
	}
	
	/**
	 * Read the result file
	 * @param filePath
	 * @return
	 * @throws java.io.IOException
	 */
	private static String readResultFile(File resultFile) throws java.io.IOException {
	    byte[] buffer = new byte[(int) resultFile.length()];
	    BufferedInputStream f = new BufferedInputStream(new FileInputStream(resultFile));
	    f.read(buffer);
	    f.close();
	    return new String(buffer);
	}	

}
