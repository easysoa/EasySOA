package org.easysoa.samples.tests.paf;

import org.easysoa.samples.paf.PureAirFlowersService;
import org.junit.Assert;
import org.junit.Test;

public class PureAirFlowersServiceTest {

	@Test
	public void testMock() {
		
		PureAirFlowersService service = new PureAirFlowersServiceMock();
		Assert.assertTrue(service.getOrdersNumber("Bob") > 0);
		
	}
	
}
