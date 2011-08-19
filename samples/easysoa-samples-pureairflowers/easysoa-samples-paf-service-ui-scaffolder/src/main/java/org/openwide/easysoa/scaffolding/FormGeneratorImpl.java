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
import org.xml.sax.InputSource;

public class FormGeneratorImpl implements FormGenerator {

	/**
	 * Logger
	 */
	private static Logger logger = Logger.getLogger(FormGeneratorImpl.class.getClass());	
	
	/* (non-Javadoc)
	 * @see org.openwide.easysoa.scaffolding.FormGenerator#generateHtmlFormFromWsdl(java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
	public String generateHtmlFormFromWsdl(String xml, String xsl, String html) /*throws Exception*/ {
		// WSDL source
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
		//TODO rename variables .... in english
		logger.debug("xml : " + xml);
		logger.debug("xsl : " + xsl);
		logger.debug("html : " + html);
				
		try{
			URL xmlUrl = new URL(xml);
			
			//SAXSource source = new SAXSource(new InputSource(new FileInputStream( new File(xml))));
			SAXSource source = new SAXSource(new InputSource(new InputStreamReader(xmlUrl.openStream())));
		
			// Output HTML file
			File fileHtml = new File(html);
			Result result = new StreamResult(fileHtml);
			
			// Transformer configuration
			TransformerFactory factory = TransformerFactory.newInstance();
			StreamSource stylesource = new StreamSource(new File(xsl));
			Transformer transformer = factory.newTransformer(stylesource);
			
			// Transformation
			transformer.transform(source, result);
			
			// Return the form
			return readForm(fileHtml);
		}
		catch(Exception ex){
			logger.error(ex);
			return "Transformation failed : " + ex.getMessage();
		}
	}
	
	/**
	 * 
	 * @param filePath
	 * @return
	 * @throws java.io.IOException
	 */
	private static String readForm(File formFile) throws java.io.IOException {
	    byte[] buffer = new byte[(int) formFile.length()];
	    BufferedInputStream f = new BufferedInputStream(new FileInputStream(formFile));
	    f.read(buffer);
	    return new String(buffer);
	}	

}
