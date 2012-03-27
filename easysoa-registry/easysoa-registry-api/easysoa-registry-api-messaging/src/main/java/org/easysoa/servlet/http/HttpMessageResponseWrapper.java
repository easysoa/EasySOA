/**
 * 
 */
package org.easysoa.servlet.http;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

/**
 * @author jguillemotte
 *
 */
public class HttpMessageResponseWrapper extends StatusExposingServletResponse {

    private CopyWriter writer;
    
    /**
     * Constructor
     * @param response
     * @throws IOException 
     * @throws UnsupportedEncodingException 
     */
    public HttpMessageResponseWrapper(HttpServletResponse response) throws UnsupportedEncodingException, IOException {
        super(response);
        // Set character encoding if no character encoding is found, default is ISO-....
        /*if("".equals(response.getCharacterEncoding())){
            response.setCharacterEncoding("UTF-8");
        }*/
        // Create copy writer.
        writer = new CopyWriter(new OutputStreamWriter(response.getOutputStream(), response.getCharacterEncoding()));
    }
    
    // TODO override the write methods to fill the ByteArrayOutputStream.
    public ServletOutputStream getOutputStream() {
        return new ServletOutputStream() {
            private ByteArrayOutputStream bos = new ByteArrayOutputStream();
            @Override
            public void write(int b) throws IOException {
                bos.write(writer.getCopy().getBytes());
            }
        };
    }

    /**
     * 
     * @return
     */
    public PrintWriter getWriter() {
        return this.writer;
    }
    
    /**
     * 
     * @return
     */
    public String getMessageContent(){
       return this.writer.getCopy();
    }
}
