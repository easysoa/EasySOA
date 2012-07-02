package org.easysoa.samples.paf;

import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

@Path("/rest")
public class PureAirFlowersRest {

    private PureAirFlowersServiceImpl pureAirFlowersServiceImpl;
    
    public PureAirFlowersRest() {
        pureAirFlowersServiceImpl = new PureAirFlowersServiceImpl();
    }
    
    @GET
    @Path("/orders/{clientName}")
    public Object getOrdersNumber(@PathParam("clientName") String clientName) {
        return pureAirFlowersServiceImpl.getOrdersNumber(clientName);
    }

    @POST
    @Path("/orders")
    public void addOrder(@FormParam("clientName") String clientName, @FormParam("orderNb") Integer orderNb) {
        pureAirFlowersServiceImpl.addOrder(orderNb, clientName);
    }

}
