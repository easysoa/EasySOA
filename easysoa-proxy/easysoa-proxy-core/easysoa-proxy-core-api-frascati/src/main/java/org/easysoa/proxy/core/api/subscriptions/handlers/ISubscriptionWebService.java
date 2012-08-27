package org.easysoa.proxy.core.api.subscriptions.handlers;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import org.easysoa.proxy.core.api.handler.event.admin.Subscriptions;
import org.osoa.sca.annotations.Remotable;

/**
 *
 * @author fntangke
 *
 */
@Remotable
public interface ISubscriptionWebService {

    @GET
    @Path("/subscriptions")
    @Produces({"application/xml", "application/json"})
    public Subscriptions getSubscriptions();

    @POST
    @Path("/subscriptions")
    @Produces({"application/xml", "application/json"})
    public Subscriptions udpateSubscriptions(Subscriptions subscriptions);

  
    @GET
    @Path("/subscriptions/essai")
    @Produces({"application/xml", "application/json"})
    public String getNumber();
}
