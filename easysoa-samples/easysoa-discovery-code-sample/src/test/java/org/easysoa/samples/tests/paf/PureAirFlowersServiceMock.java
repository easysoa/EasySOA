package org.easysoa.samples.tests.paf;

import org.easysoa.samples.paf.PureAirFlowersService;

public class PureAirFlowersServiceMock implements PureAirFlowersService {

	@Override
	public int getOrdersNumber(String clientName) {
		return 42;
	}

	@Override
	public int addOrder(Integer orderNb, String clientName) {
		return 42;
	}

}
