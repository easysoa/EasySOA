package net.webservicex;

import static org.mockito.Mockito.spy;

import java.util.logging.Logger;

import net.server.Delegated;


@javax.jws.WebService(
        serviceName = "GlobalWeather",
        portName = "GlobalWeatherSoap",
        targetNamespace = "http://www.webserviceX.NET",
        //wsdlLocation = "http://www.webservicex.net/globalweather.asmx?wsdl",
        endpointInterface = "net.webservicex.GlobalWeatherSoap")
public class GlobalWeatherWebServiceSoapSpyWrapper implements GlobalWeatherSoap, Delegated<GlobalWeatherSoap>  {

	private static final Logger LOG = Logger.getLogger(GlobalWeatherWebServiceSoapSpyWrapper.class.getName());
    
    private GlobalWeatherSoap impl;
    private GlobalWeatherSoap spyDelegate;

	public GlobalWeatherWebServiceSoapSpyWrapper() {
    	this.impl = new GlobalWeatherSoapImpl1();
    	this.spyDelegate = spy(this.impl);
    }
	
    public String getCitiesByCountry(String countryName) {
		return spyDelegate.getCitiesByCountry(countryName);
	}

	public String getWeather(String cityName, String countryName) {
		return spyDelegate.getWeather(cityName, countryName);
	}

	@Override
	public GlobalWeatherSoap getDelegate() {
		return this.spyDelegate;
	}
	
}
