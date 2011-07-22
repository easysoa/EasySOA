package pureairflowers.clients;

import javax.jws.WebParam;
import javax.jws.WebService;
import javax.jws.WebResult;

@WebService
public interface PureAirFlowersClients {

	@WebResult(name="ordersNumber")int getOrdersNumber(@WebParam(name="ClientId") String text);

    //String repeatAfterMe(@WebParam(name="text") String text, @WebParam(name="iterations") int iterations);
}
