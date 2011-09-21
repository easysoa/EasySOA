package org.openwide.easysoa.test.mock;

import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;

@WebService
public interface TalendTutoServiceMock {

	@WebResult(name="getAirportInformationByISOCountryCodeResult")String getAirportInformationByISOCountryCode(@WebParam(name="CountryAbbrviation") String countryAbbrviation);
	
}
