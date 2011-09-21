package de.sopera.airportsoap;

public class AirportSoapImpl implements AirportSoap {

	@Override
	public String getAirportInformationByISOCountryCode(String countryAbbrviation) {
		if("FR".equalsIgnoreCase(countryAbbrviation)){
			return "No informations about his country !";
		} else if("DE".equalsIgnoreCase(countryAbbrviation)){
			return "No informations about his country !";
		} else {
			return "Unkown country code ...";
		}
	}

}
