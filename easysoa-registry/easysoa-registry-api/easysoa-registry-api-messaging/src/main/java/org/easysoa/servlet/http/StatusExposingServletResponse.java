package org.easysoa.servlet.http;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

/**
 * Servlet response class with getStatus method
 * from http://stackoverflow.com/questions/1302072/how-can-i-get-the-http-status-code-out-of-a-servletresponse-in-a-servletfilter
 * @author jguillemotte
 *
 */
public class StatusExposingServletResponse extends HttpServletResponseWrapper {

    private int httpStatus;

    /**
     * Constructor
     * @param response
     */
    public StatusExposingServletResponse(HttpServletResponse response) {
        super(response);
    }

    @Override
    public void sendError(int sc) throws IOException {
        httpStatus = sc;
        super.sendError(sc);
    }

    @Override
    public void sendError(int sc, String msg) throws IOException {
        httpStatus = sc;
        super.sendError(sc, msg);
    }


    @Override
    public void setStatus(int sc) {
        httpStatus = sc;
        super.setStatus(sc);
    }

    /**
     * Returns the status
     * @return
     */
    public int getStatus() {
        return httpStatus;
    }

}

