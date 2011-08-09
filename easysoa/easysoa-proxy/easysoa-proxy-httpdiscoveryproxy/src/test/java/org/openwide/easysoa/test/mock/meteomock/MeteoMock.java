package org.openwide.easysoa.test.mock.meteomock;

import org.osoa.sca.annotations.Remotable;

@Remotable
public interface MeteoMock {

	public String getTomorrowForecast(String city);
	
}
