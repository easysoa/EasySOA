/**
 * 
 */
package com.openwide.easysoa.util;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.CharBuffer;
import org.apache.log4j.Logger;

/**
 * @author jguillemotte
 *
 */
public class ContentReader {

	/**
	 * Logger
	 */
	static Logger logger = Logger.getLogger(ContentReader.class.getName());	
	
	/**
	 * Read a <code>Reader</code> and returns it's content as a string
	 * @param reader The <code>Reader</code> to read
	 * @return 
	 */
	public static final String read(Reader reader) throws Exception {
	    StringBuffer requestBody = new StringBuffer();
	    CharBuffer buffer = CharBuffer.allocate(512); 
		while(reader.ready()){
		   	reader.read(buffer);
		   	requestBody.append(buffer.rewind());
		}
		requestBody.trimToSize();		
		//logger.debug("Reader content : " +  requestBody.toString());		
		return requestBody.toString();
	}
	
	/**
	 * Read an <code>InputStream</code> and returns it's content as a string
	 * @param stream The <code>InputStream</code> to read
	 * @return 
	 */
	public static final String read(InputStream stream) throws Exception {
	    StringBuffer requestBody = new StringBuffer();
	    CharBuffer buffer = CharBuffer.allocate(512);
	    BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
		while( reader.read(buffer) >= 0 ) {
			requestBody.append( buffer.flip() );
			buffer.clear();
		}
		//logger.debug("InputStream content : " +  requestBody.toString());
		return requestBody.toString();
	}	
	
}
