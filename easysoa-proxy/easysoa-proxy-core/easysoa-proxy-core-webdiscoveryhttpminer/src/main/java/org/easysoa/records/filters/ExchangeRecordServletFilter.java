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
import org.easysoa.records.handlers.ExchangeHandler;
import org.easysoa.servlet.http.HttpMessageRequestWrapper;
import org.easysoa.servlet.http.HttpMessageResponseWrapper;
import org.nuxeo.runtime.api.Framework;
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
    private ExchangeHandler exchangeHandler;
    
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        this.filterConfig = filterConfig;
        logger.debug("INIT called for ExchangeRecordServletFilter !!");
        //filterConfig.getInitParameter("HANDLER_RUN_NAME_PREFIX");
        try {
            FraSCAtiServiceItf frascati = Framework.getLocalService(FraSCAtiServiceProviderItf.class).getFraSCAtiService();
            // Get run manager and exchange handler services
            exchangeHandler = (ExchangeHandler) frascati.getService("composedExchangeHandler", "composedExchangeHandlerService", ExchangeHandler.class);
            runManager = (RunManager) frascati.getService("runManager", "runManagerService", RunManager.class);
            // Start a new run
            // TODO : Maybe best to use a better name            
            runManager.start(HANDLER_RUN_NAME_PREFIX + System.currentTimeMillis());
        }
        catch(Exception ex){
            // TODO : add a better error gestion
            ex.printStackTrace();
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
                exchangeHandler.handleExchange(requestWrapper, responseWrapper);
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
                // TODO : Add a better error gestion
                ex.printStackTrace();
            }
        }
    }

}
