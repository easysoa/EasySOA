
package org.easysoa.proxy.ssl.test.helloworld;

import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.POST;

public interface HelloService {

  @POST
  @Path("/{msg}")
  String print(@PathParam("msg") String msg);
  
}
