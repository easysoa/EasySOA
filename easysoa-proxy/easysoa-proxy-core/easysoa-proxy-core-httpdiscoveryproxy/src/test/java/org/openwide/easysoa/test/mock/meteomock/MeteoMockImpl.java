package org.openwide.easysoa.test.mock.meteomock;

import org.osoa.sca.annotations.Service;

@Service(MeteoMock.class)
public class MeteoMockImpl implements MeteoMock {

	@Override
	public String getTomorrowForecast(String city) {
		return "Work in progress";
	}

}
