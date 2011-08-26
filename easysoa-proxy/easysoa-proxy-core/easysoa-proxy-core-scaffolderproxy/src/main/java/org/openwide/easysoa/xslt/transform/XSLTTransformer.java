package org.openwide.easysoa.xslt.transform;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

import javax.xml.transform.Result;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.apache.log4j.Logger;
import org.xml.sax.InputSource;


public class XSLTTransformer {

	/**
	 * Logger
	 */
	private static Logger logger = Logger.getLogger(XSLTTransformer.class.getClass());	

	/**
	 * Transform a XML file with a XSLT file
	 * @param xmlSourceUrl The <code>URL</code> of the XML file to transform
	 * @param xsltFile The XLST <code>File</code> to use
	 * @param xmlOutputFile The result output <code>File</code> 
	 * @return Return the transformation result as a <code>String</code>
	 * @throws IllegalArgumentException In case of invalid parameters
	 * @throws TransformerException If the transformation fails
	 * @throws IOException If the URL or file cannot be opened
	 */
	public String transform(URL xmlSourceUrl, File xsltFile, File xmlOutputFile) throws IllegalArgumentException, TransformerException, IOException {
		// Checking parameters
		logger.debug("Transforming ....");
		if(xmlSourceUrl == null){
			throw new IllegalArgumentException("The parameter xmlSourceUrl cannot be null !");
		} 
		else if(xsltFile == null){
			throw new IllegalArgumentException("The parameter xsltFile cannot be null !");
		}
		else if(xmlOutputFile == null){
			throw new IllegalArgumentException("The parameter xmlOutputFile cannot be null !");
		}			
		// Parsing the xml source to transform
		SAXSource source = new SAXSource(new InputSource(new InputStreamReader(xmlSourceUrl.openStream())));

		// Output file
		Result result = new StreamResult(xmlOutputFile);

		// Transformer configuration
		TransformerFactory factory = TransformerFactory.newInstance();
		StreamSource stylesource = new StreamSource(xsltFile);
		Transformer transformer = factory.newTransformer(stylesource);

		// XSLT Transformation
		transformer.transform(source, result);
		
		logger.debug("Transformation done, returning result ...");
		// Return the form
		return readResultFile(xmlOutputFile);
	}
	
	/**
	 * Returns the result file as a <code>String</code>
	 * @param resultFile The result file 
	 * @return The result <code>String</code>
	 * @throws java.io.IOException
	 */
	private static String readResultFile(File resultFile) throws java.io.IOException {
	    byte[] buffer = new byte[(int) resultFile.length()];
	    BufferedInputStream f = new BufferedInputStream(new FileInputStream(resultFile));
	    f.read(buffer);
	    return new String(buffer);
	}	
}
