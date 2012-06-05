/**
 * 
 */
package org.easysoa.cxf;

import javax.ws.rs.GET;
import javax.ws.rs.Path;

/**
 * @author jguillemotte
 *
 */
public class ServerTestImpl implements ServerTest {

    /* (non-Javadoc)
     * @see org.easysoa.cxf.ServerTest#testMethod()
     */
    @Override
    @GET
    @Path("/test")    
    public String testMethod() {
        return "This is a  test";
    }

}
