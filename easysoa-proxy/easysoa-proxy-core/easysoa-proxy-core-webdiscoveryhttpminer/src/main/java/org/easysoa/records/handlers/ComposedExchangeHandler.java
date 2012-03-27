/**
 * 
 */
package org.easysoa.records.handlers;

import java.io.IOException;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author jguillemotte
 *
 */
public class ComposedExchangeHandler implements ExchangeHandler {

    // TODO : add configuration in composite file
    
    //@Reference
    private List<ExchangeHandler> exchangeHandlers;

    /**
     * Handle the exchange. Call the handle method for all handlers from the exchange handler list
     * @param request HTTP Servlet request
     * @param response HTTP servlet Response
     * @throws IOException 
     */
    public void handleExchange(HttpServletRequest request, HttpServletResponse response) throws IOException{
        if(exchangeHandlers != null){
            for(ExchangeHandler handler : exchangeHandlers){
                handler.handleExchange(request, response);
            }
        }
    }

}
