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

package org.openwide.easysoa.test.mock.meteomock;

import java.io.File;
import java.io.FileInputStream;
import org.apache.log4j.Logger;
import org.mortbay.log.Log;
import org.osoa.sca.annotations.Service;
import com.openwide.easysoa.util.ContentReader;

@Service(MeteoMock.class)
public class MeteoMockImpl implements MeteoMock {

	// Logger
	private static Logger logger = Logger.getLogger(MeteoMockImpl.class.getName());
	
	@Override
	public String getTomorrowForecast(String city) {
		// Open the response file with name equals to 'city + MeteoMockResponse.xml'
		Log.debug("Meteo forecast asked for " + city);
		String response;
		try{
			File xmlResponseFile = new File("src/test/resources/meteoMockMessages/" +  city.toLowerCase() + "MeteoMockResponse.xml");
			response = ContentReader.read(new FileInputStream(xmlResponseFile));
			response = response.substring(response.indexOf("<ns1:return>"), response.lastIndexOf("</ns1:return>"));
		}
		catch(Exception ex){
			response = "Sorry, No Meteo forecast for this city was found !";
			ex.printStackTrace();
		}
		logger.debug("response = " + response);
		return response;
	}

}
