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
import org.easysoa.message.InMessage;
import org.easysoa.message.OutMessage;
import org.easysoa.proxy.core.api.exchangehandler.HttpExchangeHandler;
import org.easysoa.proxy.core.api.exchangehandler.MessageRecordHandler;
import org.easysoa.proxy.core.api.run.RunManager;
import org.easysoa.servlet.http.CopyHttpServletRequest;
import org.easysoa.servlet.http.CopyHttpServletResponse;
import org.nuxeo.runtime.api.Framework;

/**
 * Exchange record handler : register a record from HttpServletRequest and HttpServletResponse
 * To be used in embedded Nuxeo FraSCAti 
 * 
 * @author jguillemotte
 */
public class NuxeoMessageExchangeRecordHandler extends MessageRecordHandler implements HttpExchangeHandler {

    /**
     * Constructor
     * @throws FraSCAtiServiceException If a problem occurs
     */
    public NuxeoMessageExchangeRecordHandler() throws FraSCAtiServiceException{
        super();
        // Get the run manager service
        FraSCAtiServiceItf frascati = Framework.getLocalService(FraSCAtiServiceProviderItf.class).getFraSCAtiService();
        //this.setRunManager(frascati.getService("httpDiscoveryProxy/runManagerComponent", "runManagerService", RunManager.class));
        this.setRunManager(frascati.getService("handlerManager/handlerManagerServiceBaseComp/runManagerComponent", "runManagerService", RunManager.class));
    }
    
    /* (non-Javadoc)
     * @see org.easysoa.records.handlers.ExchangeHandler#handleExchange(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
     */
    //@Override
    public void handleExchange(HttpServletRequest request, HttpServletResponse response) throws Exception {
        // Builds a new Exchange record with data contained in request and response
        CopyHttpServletRequest requestWrapper = new CopyHttpServletRequest(request);
        CopyHttpServletResponse responseWrapper;
        if (response instanceof CopyHttpServletResponse) {
            responseWrapper = (CopyHttpServletResponse) response;
        } else {
            responseWrapper = new CopyHttpServletResponse(response);
        }
        InMessage inMessage = new InMessage(requestWrapper); // TODO this and below rather in CopyOut.close();
        OutMessage outMessage = new OutMessage(responseWrapper);
        this.handleMessage(inMessage, outMessage);
    }

}
