/**
 * EasySOA Registry Rest Miner
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

/**
 * Servlet filter to record exchanges in Easysoa.
 * 
 * @author jguillemotte, mkalam-alami
 * 
 */
public class ExchangeRecordServletFilterImpl implements Filter, ExchangeRecordServletFilter {

    // Logger
    private static Logger logger = Logger.getLogger(ExchangeRecordServletFilterImpl.class);

    // Singleton
    private static ExchangeRecordServletFilterImpl singleton = null;

    // Exchange handler
    private HttpExchangeHandler exchangeHandler = null;

    /**
     * Initialize the filter
     */
    // @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        singleton = this;
        // Registering the event receiver
        try {
            FraSCAtiServiceItf frascati = Framework.getLocalService(
                    FraSCAtiServiceProviderItf.class).getFraSCAtiService();
            RunManager runManager = frascati.getService("httpDiscoveryProxy/runManagerComponent",
                    "runManagerService", RunManager.class);
            runManager.addEventReceiver(new ExchangeRecordServletFilterEventReceiver());
        } catch (Exception ex) {
            logger.error("Unable to register the ExchangeRecordServletFilterEventReceiver in the run manager", ex);
        }
    }

    /**
     * Process the filter
     */
    // @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        // Forward to the exchange handler
        if (exchangeHandler != null) {
            // Filtering HTTP requests only for registering exchanges
            logger.info("Filtering a EasySOA API request");
            if (request instanceof HttpServletRequest) {
                HttpMessageRequestWrapper requestWrapper = new HttpMessageRequestWrapper((HttpServletRequest) request);
                HttpMessageResponseWrapper responseWrapper = new HttpMessageResponseWrapper((HttpServletResponse) response);
                try {
                    exchangeHandler.handleExchange(requestWrapper, responseWrapper);
                } catch (Exception e) {
                    logger.error("An error occurred during the exchange handling", e);
                }
            }
        }
        // Let the request continue
        chain.doFilter(request, response);
    }

    /**
     * Destroy the filter
     */
    // @Override
    public void destroy() {
        // Nothing to do
    }

    /**
     * Start the mining
     */
    public void start(HttpExchangeHandler exchangeHandler) throws Exception {
        // NOTE: We probably can't make start() and stop() static
        // as the caller will be in a separate classloader.

        if (singleton != null) {
            logger.info("Starting mining with handler " + exchangeHandler.toString());
            singleton.exchangeHandler = exchangeHandler;
        } else {
            logger.warn("Can't start mining, the filter is not ready yet");
        }
    }

    /**
     * Stop the mining
     */
    public void stop() {
        if (singleton != null) {
            logger.info("Stopping mining");
            singleton.exchangeHandler = null;
        } else {
            logger.info("Nothing to stop, the filter is not ready yet");
        }
    }

}
