/**
 * 
 */
package org.openwide.easysoa.test.mock;

/**
 * @author jguillemotte
 *
 */
public class TalendTutoServiceMockImpl implements TalendTutoServiceMock {

	@Override
	public String getAirportInformationByISOCountryCode(String countryAbbrviation) {
		if("FR".equalsIgnoreCase(countryAbbrviation)){
			return "LFLL - Lyon Saint Exupery, LFPB - Paris Le Bourget, LFPO - Paris Orly, LFPG - Paris Charles de Gaulle, LFBD - Bordeaux Merignac";
		} else if("DE".equalsIgnoreCase(countryAbbrviation)){
			return "EDMO - Munich Oberpfaffenhofen, EDDB - Berlin Schonefeld, EDDI - Berlin Tempelhof, EDDK - Cologne Koln Bonn";
		} else {
			return "Unkown country code ...";
		}
	}

	/* (non-Javadoc)
	 * @see org.openwide.easysoa.test.mock.TalendTutoServiceMock#getAirportInformationByISOCountryCode(java.lang.String)
	 */
	/*@Override
	
	public String getAirportInformationByISOCountryCode(String countryCode) {
		if("FR".equalsIgnoreCase(countryCode)){
			return "No informations about his country !";
		} else if("DE".equalsIgnoreCase(countryCode)){
			return "No informations about his country !";
		} else {
			return "Unkown country code ...";
		}
	}*/

}
