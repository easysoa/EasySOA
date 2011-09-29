package org.openwide.easysoa.scaffolding;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.net.URL;

import javax.xml.transform.Result;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.apache.log4j.Logger;
import org.osoa.sca.annotations.Property;
import org.xml.sax.InputSource;

/**
 * Transform a WSDL into an HTML form with an XSLT transformation
 * 
 * @author jguillemotte
 *
 */
public class XsltFormGenerator implements FormGenerator {

	/**
	 * Logger
	 */
	private static Logger logger = Logger.getLogger(XsltFormGenerator.class.getClass());	

	@Property
	String defaultWsdl;
		
	/* (non-Javadoc)
	 * @see org.openwide.easysoa.scaffolding.FormGenerator#generateHtmlFormFromWsdl(java.lang.String, java.lang.String, java.lang.String)
	 */
	@Override
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
			// Parsing XML
			// Can works with HTTP protocol (http://...) or FILE protocol (file://...)
			SAXSource source;
			if(formWsdlXmlSource != null){
				URL formWsdlXmlUrl = new URL(formWsdlXmlSource);
				source = new SAXSource(new InputSource(new InputStreamReader(formWsdlXmlUrl.openStream())));
			}
			// Hack to works with Talend tutorial !!
			else {
				File wsdlFile = new File(defaultWsdl);
				source = new SAXSource(new InputSource(new FileReader(wsdlFile)));
			}
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
			logger.error(ex);
			return "Transformation failed : " + ex.getMessage();
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
