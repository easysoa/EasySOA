package org.easysoa.samples.paf;

import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

/**
 * Allows for orders management of the PureAirFlowers clients.
 * - All clients are specified by a unique client name
 * - Orders count are represented by a integer
 * This documentation is not 40 lines long, but should be enough
 * to reach a moderately satisfying result for the services documentation
 * indicator.
 * 
 * @author jguillemotte
 */
@Path("/rest")
public class PureAirFlowersRest {

    private PureAirFlowersServiceImpl pureAirFlowersServiceImpl;
    
    public PureAirFlowersRest() {
        pureAirFlowersServiceImpl = new PureAirFlowersServiceImpl();
    }

    /**
     * Returns the orders number for the specified client name.
     */
    @GET
    @Path("/orders/{clientName}")
    public Object getOrdersNumber(@PathParam("clientName") String clientName) {
        return pureAirFlowersServiceImpl.getOrdersNumber(clientName);
    }

    /**
     * Adds an order to the specified client
     */
    @POST
    @Path("/orders")
    public void addOrder(@FormParam("clientName") String clientName, @FormParam("orderNb") Integer orderNb) {
        pureAirFlowersServiceImpl.addOrder(orderNb, clientName);
    }

}
