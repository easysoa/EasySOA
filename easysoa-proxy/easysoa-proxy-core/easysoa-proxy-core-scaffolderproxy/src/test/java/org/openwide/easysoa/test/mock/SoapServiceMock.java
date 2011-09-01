package org.openwide.easysoa.test.mock;

import org.osoa.sca.annotations.Remotable;

@Remotable
public interface SoapServiceMock {

	public String getPrice(String product, int quantity);

}
