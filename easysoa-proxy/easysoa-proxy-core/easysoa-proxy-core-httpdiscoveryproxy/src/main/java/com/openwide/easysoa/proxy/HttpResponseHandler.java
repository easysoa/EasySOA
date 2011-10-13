package com.openwide.easysoa.proxy;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.log4j.Logger;
import java.util.zip.GZIPInputStream;
import java.util.zip.ZipInputStream;

/**
 * Response handler with deflate/uncompress (GZIP and ZIP only) feature, if the response content is not compressed, simply returns a string
 * @author jguillemotte
 *
 */
public class HttpResponseHandler implements ResponseHandler<String> {

	/**
	 * Logger
	 */
	private Logger logger = Logger.getLogger(HttpResponseHandler.class.getName());	
	
	@Override
	public String handleResponse(HttpResponse response) throws ClientProtocolException, IOException {
		Header contentEncodingHeader = response.getFirstHeader("Content-Encoding");		
		StringBuffer responseBuffer = new StringBuffer();
		if(contentEncodingHeader != null){
            byte[] buf = new byte[512];
            int len;
            ByteArrayOutputStream bos = new ByteArrayOutputStream();            
			if("gzip".equalsIgnoreCase(contentEncodingHeader.getValue())){
				// deflate compressed gzip response
				GZIPInputStream gzipInputStream = new GZIPInputStream(response.getEntity().getContent());
                while ((len = gzipInputStream.read(buf, 0, 512)) != -1){
                	bos.write(buf, 0, len);
				}
		    	responseBuffer.append(new String(bos.toByteArray()));
			} else if("zip".equalsIgnoreCase(contentEncodingHeader.getValue())){
				// deflate compressed zip response
				ZipInputStream zipInputStream = new ZipInputStream(response.getEntity().getContent());
                while ((len = zipInputStream.read(buf, 0, 512)) != -1){
                	bos.write(buf, 0, len);
				}
		    	responseBuffer.append(new String(bos.toByteArray()));
			} else {
				logger.error("Unable to deflate this Content-Encoding : " + contentEncodingHeader.getValue());
				throw new IOException("Unable to deflate this Content-Encoding : " + contentEncodingHeader.getValue());
			}
		} else {
			// Read the response message content
			InputStreamReader in= new InputStreamReader(response.getEntity().getContent());
			BufferedReader bin= new BufferedReader(in);
			String line;
			do{
				 line = bin.readLine();
				 if(line != null){
					 responseBuffer.append(line); 
				 }
			}
			while(line != null);
		}
		return responseBuffer.toString();
	}

}
