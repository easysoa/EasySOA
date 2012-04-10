/**
 * EasySOA Samples - Smart Travel
 * Copyright 2011 Open Wide
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * 
 * Contact : easysoa-dev@googlegroups.com
 */

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
