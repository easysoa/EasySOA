package org.openwide.easysoa.test.mock;

public class SoapServiceMockImpl implements SoapServiceMock {

	@Override
	public String getPrice(String product, int quantity) {
		float price = 10 * quantity;
		return "" + price;
	}
	
}
