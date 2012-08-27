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

/**
 * 
 */
package org.easysoa.proxy.core.api.util;

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
		while( reader.read(buffer) >= 0 ) {
			requestBody.append(buffer.flip());
			buffer.clear();			
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
