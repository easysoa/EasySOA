/**
 * 
 */
package org.easysoa.servlet.http;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.StringReader;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import org.apache.commons.io.IOUtils;

/**
 * 
 * @author jguillemotte
 *
 */
public class HttpMessageRequestWrapper extends HttpServletRequestWrapper {

    // Request content
    final private String reqContent;
    
    /**
     * 
     * @param request
     * @throws IOException
     */
    public HttpMessageRequestWrapper(HttpServletRequest request) throws IOException {
        super(request);
        reqContent = IOUtils.toString(request.getInputStream());    
    }
    
    /**
     * 
     * @return
     */
    public ServletInputStream getInputStream() {
        return new ServletInputStream() {
            private ByteArrayInputStream bis = new ByteArrayInputStream(reqContent.getBytes());
            @Override
            public int read() throws IOException {
                return bis.read();
            }
        };
    }    
    
    /**
     * 
     * @return
     */
    public BufferedReader getReader() {
        return new BufferedReader(new StringReader(reqContent));
    }
    
}
