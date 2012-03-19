/**
 * 
 */
package org.easysoa.records.handlers;

import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author jguillemotte
 *
 */
public class ComposedExchangeHandler {

    // TODO : add configuration in composite file
    
    //@Reference
    private List<ExchangeHandler> exchangeHandlerList;

    /**
     * Handle the exchange. Call the handle method for all handlers from the exchange handler list
     * @param request HTTP Servlet request
     * @param response HTTP servlet Response
     */
    public void handleExchange(HttpServletRequest request, HttpServletResponse response){
        if(exchangeHandlerList != null){
            for(ExchangeHandler handler : exchangeHandlerList){
                handler.handleExchange(request, response);
            }
        }
    }

}
