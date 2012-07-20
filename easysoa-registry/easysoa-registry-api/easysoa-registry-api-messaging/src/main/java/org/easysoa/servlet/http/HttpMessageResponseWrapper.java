/**
 * EasySOA Registry
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
package org.easysoa.servlet.http;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

/**
 * @author jguillemotte
 *
 */
public class HttpMessageResponseWrapper extends StatusExposingServletResponse {

    private PrintWriter writer;
    private CopyOutputStream copyOut;
    
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
        //writer = new CopyWriter(new OutputStreamWriter(response.getOutputStream(), response.getCharacterEncoding()));
    }
    
    // TODO override the write methods to fill the ByteArrayOutputStream.
    public ServletOutputStream getOutputStream() throws IOException {
        copyOut = new CopyOutputStream(this.getResponse().getOutputStream());
        writer = new PrintWriter(new OutputStreamWriter(copyOut, this.getCharacterEncoding()));
        return new ServletOutputStream() {
            @Override
            public void write(int b) throws IOException {
                //bos.write(writer.getCopy().getBytes());
                copyOut.write(b);
            }
        };
    }

    /**
     * 
     * @return
     */
    public PrintWriter getWriter() throws IOException {
        copyOut = new CopyOutputStream(this.getResponse().getOutputStream());
        writer = new PrintWriter(new OutputStreamWriter(copyOut, this.getCharacterEncoding()));
        return this.writer;
    }
    
    /**
     * 
     * @return
     */
    public String getMessageContent(){
       return new String(this.copyOut.getCopy(), Charset.forName(this.getCharacterEncoding())); // ??
    }
}
