package org.easysoa.samples.paf;

import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;

@WebService
public interface PureAirFlowersService {

	@WebResult(name = "ordersNumber")
	public abstract int getOrdersNumber(
			@WebParam(name = "ClientName") String clientName);

	@WebResult(name = "addOrder")
	public abstract int addOrder(@WebParam(name = "orderNb") Integer orderNb,
			@WebParam(name = "ClientName") String clientName);

}