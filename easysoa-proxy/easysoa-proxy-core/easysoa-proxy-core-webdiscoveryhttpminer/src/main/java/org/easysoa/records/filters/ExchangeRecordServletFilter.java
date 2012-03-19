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
import org.easysoa.frascati.api.FraSCAtiServiceItf;
import org.easysoa.frascati.api.FraSCAtiServiceProviderItf;
import org.easysoa.records.ExchangeRecord;
import org.easysoa.records.handlers.ComposedExchangeHandler;
import org.easysoa.servlet.http.StatusExposingServletResponse;
import org.nuxeo.runtime.api.Framework;
import com.openwide.easysoa.message.InMessage;
import com.openwide.easysoa.message.OutMessage;

//import org.easysoa.registry.frascati.NxFraSCAtiRegistryService;
//import com.google.inject.Inject;

/**
 * @author jguillemotte
 *
 */
public class ExchangeRecordServletFilter implements Filter {

    //@Inject
    //NxFraSCAtiRegistryService frascatiRegistryService;    
    
    //TODO : add a reference to run manager
    // Maybe it will be necessary to move the run package (today in HTTP discovery proxy project) in another project
    
    // Filter configuration
    private FilterConfig filterConfig;
    
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        this.filterConfig = filterConfig;
        
        //TODO : Start a new run
        
     }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        
        // TODO : Call a component to get an exchange Handler and call the method handle Exchange instead to have the code directly in tthis class
        // TODO : must be configurable
        ComposedExchangeHandler composedExchangeHandler = new ComposedExchangeHandler();
        
        // Filtering HTTP requests only for registering exchanges
        if(request instanceof HttpServletRequest){ 
            //StatusExposingServletResponse sevResponse = new StatusExposingServletResponse((HttpServletResponse)response);
            chain.doFilter(request, response);
            composedExchangeHandler.handleExchange((HttpServletRequest)request, (HttpServletResponse)response);
        }
        else {
            chain.doFilter(request, response);
        }
    }

    @Override
    public void destroy() {
        filterConfig = null;
        //TODO : stop the run and save the records
        
    }

}
