/**
 * 
 */
package org.easysoa.records.handlers;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author jguillemotte
 *
 */
public interface ExchangeHandler {
    
    /**
     * Handle the exchange
     * @param request HTTP Servlet request
     * @param response HTTP Servlet response
     */
    public void handleExchange(HttpServletRequest request, HttpServletResponse response);
    
}
