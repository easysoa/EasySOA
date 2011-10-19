/**
 * EasySOA Proxy
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
