package org.openwide.easysoa.test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
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
	
}
