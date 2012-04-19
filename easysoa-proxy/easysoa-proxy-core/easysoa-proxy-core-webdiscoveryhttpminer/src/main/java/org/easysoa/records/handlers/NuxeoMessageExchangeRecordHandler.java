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
package org.easysoa.records.handlers;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.easysoa.frascati.FraSCAtiServiceException;
import org.easysoa.frascati.api.FraSCAtiServiceItf;
import org.easysoa.frascati.api.FraSCAtiServiceProviderItf;
import org.easysoa.servlet.http.HttpMessageRequestWrapper;
import org.easysoa.servlet.http.HttpMessageResponseWrapper;
import org.nuxeo.runtime.api.Framework;
import com.openwide.easysoa.exchangehandler.HttpExchangeHandler;
import com.openwide.easysoa.exchangehandler.MessageRecordHandler;
import com.openwide.easysoa.message.InMessage;
import com.openwide.easysoa.message.OutMessage;
import com.openwide.easysoa.run.RunManager;

/**
 * Exchange record handler : register a record from HttpServletRequest and HttpServletResponse  
 * 
 * @author jguillemotte
 */
public class NuxeoMessageExchangeRecordHandler extends MessageRecordHandler implements HttpExchangeHandler {

    public NuxeoMessageExchangeRecordHandler() throws FraSCAtiServiceException{
        super();
     // Get the service
        FraSCAtiServiceItf frascati = Framework.getLocalService(FraSCAtiServiceProviderItf.class).getFraSCAtiService();
       this.setRunManager((RunManager) frascati.getService("runManager", "runManagerService", RunManager.class));
    }

    /* (non-Javadoc)
     * @see org.easysoa.records.handlers.ExchangeHandler#handleExchange(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    @Override
    public void handleExchange(HttpServletRequest request, HttpServletResponse response) throws Exception {
        // Builds a new Exchange record with data contained in request and response
        HttpMessageRequestWrapper requestWrapper = new HttpMessageRequestWrapper(request);
        HttpMessageResponseWrapper responseWrapper = new HttpMessageResponseWrapper(response);
        InMessage inMessage = new InMessage(requestWrapper);
        OutMessage outMessage = new OutMessage(responseWrapper);
        this.handleExchange(inMessage, outMessage);
    }

}
