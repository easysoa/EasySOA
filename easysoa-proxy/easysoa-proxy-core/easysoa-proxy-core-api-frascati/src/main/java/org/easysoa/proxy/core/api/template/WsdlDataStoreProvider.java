/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.easysoa.proxy.core.api.template;

import java.io.IOException;
import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;

/**
 *
 * @author jguillemotte
 */
public interface WsdlDataStoreProvider {

    // TODO : inject velocity components to render store list or WSDL
    // TODO : ProxyFileStore as Reference, no proxyFileStore composite at this time
    // @Reference
    // protected ProxyFileStore proxyFileStore;
    //void buildResponse(String requestedResource, VelocityEngine velocityEngine, VelocityContext velocityContext, HttpServletRequest request, HttpServletResponse response) throws IOException;

    /**
     * Build a response containing the HTML formated store list
     * @param request The http request
     * @param response The http response to build
     * @Param templateReplayWSDLHTMLListRenderer The template renderer
     * @throws IOException If a problem occurs
     */
    public void buildStoreListHTMLResponse(HttpServletRequest request, HttpServletResponse response, Servlet templateReplayWSDLHTMLListRenderer) throws IOException, ServletException;
    
    /**
     * Build a response containing a WSDL file. The WSDL file is generated from
     * a FLD file processed by a velocity template
     * @param request The http request
     * @param response The http response to build
     * @param templateReplayWSDLRenderer The template renderer
     * @throws IOException If a problem occurs
     */
    public void buildWSDLResponse(HttpServletRequest request, HttpServletResponse response, Servlet templateReplayWSDLRenderer) throws IOException, ServletException, Exception;
    
}
