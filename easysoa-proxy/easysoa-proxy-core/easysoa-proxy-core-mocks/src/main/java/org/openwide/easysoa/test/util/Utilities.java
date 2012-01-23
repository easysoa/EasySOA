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

package org.openwide.easysoa.test.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;

public class Utilities {

	/**
	 * Read a response file and returns the content 
	 * @return The content of the response file, an error message otherwise
	 */
	public final static String readResponseFile(String responseFileUri){
		try {
			File responseFile;
			responseFile = new File(responseFileUri);
			BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(responseFile)));
			StringBuffer response = new StringBuffer();
			while(reader.ready()){
				response.append(reader.readLine());
			}
			return response.toString();	
		}
		catch(Exception ex){
			ex.printStackTrace();
			return "Unable to read default response file ...";
		}
	}
	
	/**
	 * Read a response file and returns the content 
	 * @return The content of the response file, an error message otherwise
	 */
	public final static String readResponseStream(InputStream inputStream){
		try {
			BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
			StringBuffer response = new StringBuffer();
			while(reader.ready()){
				response.append(reader.readLine());
			}
			return response.toString();	
		}
		catch(Exception ex){
			ex.printStackTrace();
			return "Unable to read default response file ...";
		}
	}	
	
}
