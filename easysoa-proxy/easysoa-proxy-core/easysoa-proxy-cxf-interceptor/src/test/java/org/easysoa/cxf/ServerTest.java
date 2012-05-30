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
public interface ServerTest {

    // Original methods
    @GET
    @Path("/test")
    public String testMethod();
    
}
