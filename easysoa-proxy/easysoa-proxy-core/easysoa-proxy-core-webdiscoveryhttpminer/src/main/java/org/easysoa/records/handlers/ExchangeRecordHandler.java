/**
 * 
 */
package org.easysoa.records.handlers;

import java.io.IOException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.easysoa.frascati.api.FraSCAtiServiceItf;
import org.easysoa.frascati.api.FraSCAtiServiceProviderItf;
import org.easysoa.records.ExchangeRecord;
import org.easysoa.servlet.http.HttpMessageRequestWrapper;
import org.easysoa.servlet.http.HttpMessageResponseWrapper;
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
    public void handleExchange(HttpServletRequest request, HttpServletResponse response) throws Exception {
        // Builds a new Exchange record with data contained in request and response
        ExchangeRecord record = new ExchangeRecord();
        HttpMessageRequestWrapper requestWrapper = new HttpMessageRequestWrapper(request);
        HttpMessageResponseWrapper responseWrapper = new HttpMessageResponseWrapper(response);
        InMessage inMessage = new InMessage(requestWrapper);
        OutMessage outMessage = new OutMessage(responseWrapper);
        record.setInMessage(inMessage);
        record.setOutMessage(outMessage);
        // Get the service
        FraSCAtiServiceItf frascati = Framework.getLocalService(FraSCAtiServiceProviderItf.class).getFraSCAtiService();
        RunManager runManager = (RunManager) frascati.getService("runManager", "runManagerService", RunManager.class);
        // Call runManager to register the exchange record 
        runManager.record(record);
    }

}
