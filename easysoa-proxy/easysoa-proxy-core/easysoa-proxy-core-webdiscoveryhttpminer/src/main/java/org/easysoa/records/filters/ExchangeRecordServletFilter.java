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
package org.easysoa.records.filters;

import java.io.IOException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.log4j.Logger;
import org.easysoa.frascati.api.FraSCAtiServiceItf;
import org.easysoa.frascati.api.FraSCAtiServiceProviderItf;
import org.easysoa.servlet.http.HttpMessageRequestWrapper;
import org.easysoa.servlet.http.HttpMessageResponseWrapper;
import org.nuxeo.runtime.api.Framework;

import com.openwide.easysoa.exchangehandler.HttpExchangeHandler;
import com.openwide.easysoa.run.RunManager;

//import org.easysoa.registry.frascati.NxFraSCAtiRegistryService;
//import com.google.inject.Inject;

/**
 * @author jguillemotte
 *
 */
public class ExchangeRecordServletFilter implements Filter {

    //@Inject
    //NxFraSCAtiRegistryService frascatiRegistryService;    
    
    private static Logger logger = Logger.getLogger(ExchangeRecordServletFilter.class.getClass());    
    
    // TODO : use the filterConfig instead of static string for run mane prefix
    public final static String HANDLER_RUN_NAME_PREFIX = "handler_run_"; 
    
    // Filter configuration
    private FilterConfig filterConfig;
    
    // Run manager
    private RunManager runManager;

    // Exchange handler
    private HttpExchangeHandler exchangeHandler;
    
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        this.filterConfig = filterConfig;
        logger.debug("INIT called for ExchangeRecordServletFilter !!");
        //filterConfig.getInitParameter("HANDLER_RUN_NAME_PREFIX");
        try {
            FraSCAtiServiceItf frascati = Framework.getLocalService(FraSCAtiServiceProviderItf.class).getFraSCAtiService();
            // Get run manager and exchange handler services
            exchangeHandler = (HttpExchangeHandler) frascati.getService("composedExchangeHandler", "composedExchangeHandlerService", HttpExchangeHandler.class);
            runManager = (RunManager) frascati.getService("runManager", "runManagerService", RunManager.class);
            // Start a new run
            // TODO : Maybe best to use a better name            
            runManager.start(HANDLER_RUN_NAME_PREFIX + System.currentTimeMillis());
        }
        catch(Exception ex){
            ex.printStackTrace();
            logger.debug("An error occurs during the initialization of the filter", ex);
        }
     }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        logger.debug("Entering in doFilter method for ExchangeRecordServletFilter !!");
        // Filtering HTTP requests only for registering exchanges
        if(request instanceof HttpServletRequest){ 
            HttpMessageRequestWrapper requestWrapper = new HttpMessageRequestWrapper((HttpServletRequest)request);
            HttpMessageResponseWrapper responseWrapper = new HttpMessageResponseWrapper((HttpServletResponse)response);
            chain.doFilter(requestWrapper, responseWrapper);
            if(exchangeHandler != null){
                try {
                    exchangeHandler.handleExchange(requestWrapper, responseWrapper);
                }
                catch(Exception ex){
                    logger.error("An error occurs during the exchange handling", ex);
                }
            }
        }
        else {
            chain.doFilter(request, response);
        }
    }

    @Override
    public void destroy() {
        filterConfig = null;
        if(runManager != null){
            try {
                runManager.stop();
                runManager.save();
                runManager.delete();
            }
            catch(Exception ex){
                ex.printStackTrace();
                logger.debug("An error occurs during the creation of the echange record store", ex);
            }
        }
    }

}
