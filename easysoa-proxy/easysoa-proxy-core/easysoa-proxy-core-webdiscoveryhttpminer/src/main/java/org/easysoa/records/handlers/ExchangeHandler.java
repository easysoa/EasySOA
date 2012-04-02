/**
 * 
 */
package org.easysoa.records.handlers;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author jguillemotte
 *
 */
public interface ExchangeHandler {
    
    /**
     * Handle an exchange
     * @param request HTTP Servlet request
     * @param response HTTP Servlet response
     * @throws Exception 
     */
    public void handleExchange(HttpServletRequest request, HttpServletResponse response) throws Exception;
    
}
