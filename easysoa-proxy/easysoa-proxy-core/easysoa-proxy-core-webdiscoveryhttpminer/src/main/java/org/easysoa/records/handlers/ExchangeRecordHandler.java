/**
 * 
 */
package org.easysoa.records.handlers;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.StringReader;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

import org.apache.commons.io.IOUtils;
import org.easysoa.frascati.api.FraSCAtiServiceItf;
import org.easysoa.frascati.api.FraSCAtiServiceProviderItf;
import org.easysoa.records.ExchangeRecord;
import org.nuxeo.runtime.api.Framework;

import com.openwide.easysoa.message.InMessage;
import com.openwide.easysoa.message.OutMessage;
import com.openwide.easysoa.run.RunManager;

/**
 * Exchange record handler : register a record from HttpServletRequest and HttpServletResponse  
 * 
 * @author jguillemotte
 */
public class ExchangeRecordHandler implements ExchangeHandler {

    /* (non-Javadoc)
     * @see org.easysoa.records.handlers.ExchangeHandler#handleExchange(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    @Override
    public void handleExchange(HttpServletRequest request, HttpServletResponse response) {
        ExchangeRecord record = new ExchangeRecord();
        InMessage inMessage = new InMessage((HttpServletRequest)request);
        //TODO : Complete the OutMessage with response content
        HttpServletResponseWrapper responseWrapper = new HttpServletResponseWrapper(response);
        
        // TODO : Make a generic wrapper from this code
        // to be used in servletFilter to get response content
        
        // wrapping request to cache content, because
        // 1. otherwise in test.record(), new InMessage(request) won't be able to read its content (??!!)
        // and 2. it eases debugging but could also allow test assertions such as :
        //String requestContent = new Scanner(req.getInputStream()).useDelimiter("\\A").next();
        /*
        final String reqContent = IOUtils.toString(request.getInputStream());
        ///System.err.println("\n\nreq content:\n" + reqContent);
        
        request = new HttpServletRequestWrapper(request) {
            public ServletInputStream getInputStream() {
                return new ServletInputStream() {
                    private ByteArrayInputStream bis = new ByteArrayInputStream(reqContent.getBytes());
                    @Override
                    public int read() throws IOException {
                        return bis.read();
                    }
                };
            }
            public BufferedReader getReader() {
                return new BufferedReader(new StringReader(reqContent));
            }
        };
        */
        
        // Get the response content ????
        System.out.println("DEBUG : How to have the response content : " + responseWrapper.toString());
        // TODO : maybe better to pass a Wrapper instead of directly the response
        OutMessage outMessage = new OutMessage(response);
        
        record.setInMessage(inMessage);
        record.setOutMessage(outMessage);
        // Call runManager to register the exchange record
        try{
            // Get the service
            FraSCAtiServiceItf frascati = Framework.getLocalService(FraSCAtiServiceProviderItf.class).getFraSCAtiService();
            RunManager runManager = (RunManager) frascati.getService("runManager", "runManagerService", RunManager.class);
            runManager.record(record);
        }
        catch(Exception ex) {
            // TODO add a better error gestion
            ex.printStackTrace();
        }
    }

}
