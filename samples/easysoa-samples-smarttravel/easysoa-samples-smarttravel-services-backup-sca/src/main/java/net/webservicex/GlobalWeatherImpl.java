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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Date;
import java.util.Random;

//import org.osoa.sca.annotations.Scope;
//import org.osoa.sca.annotations.Service;

//@Scope("COMPOSITE")
//@Service(interfaces={net.webservicex.IGlobalWeather.class})
public class GlobalWeatherImpl implements IGlobalWeather {

	// ////////////////////////////////////////////////////
	// Fields
	// ////////////////////////////////////////////////////

	private Random rnd;
	private String cities_fr;
	private String cities_other;
	private String weather_format;
	private String[] skyConditions = { "mostly cloudy", "mostly clear", "overcast", "partly cloudy", "clear" };

	// ////////////////////////////////////////////////////
	// Constructor
	// ////////////////////////////////////////////////////

	public GlobalWeatherImpl() {
		rnd = new Random();
		InputStream in = this.getClass().getClassLoader().getResourceAsStream("cities-fr.txt");

		cities_fr = convertStreamToString(in);
		cities_other = "<string></string>";
		
		in = this.getClass().getClassLoader().getResourceAsStream("weather-format.txt");
		weather_format = convertStreamToString(in);
	}

	// ////////////////////////////////////////////////////
	// GlobalWeather methods
	// ////////////////////////////////////////////////////

	public String getCitiesByCountry(String countryName) {
		if (countryName.equalsIgnoreCase("France")) {
			return cities_fr;
		} else {
			return cities_other;
		}
	}

	public String getWeather(String cityName, String countryName) {
		Date now = new Date();
		return String.format(weather_format, cityName, countryName, now.toString(), getRandomSkyConditions(), getRandomTemperature());
	}

	// ////////////////////////////////////////////////////
	// Helper methods
	// ////////////////////////////////////////////////////

	private String getRandomTemperature() {
		int tempC = rnd.nextInt(40);
		// celsius to fahrenheit
		int tempF = (int) ((9f / 5f) * (float) tempC + 32f);

		return tempF + " F (" + tempC + " C)";
	}

	private String getRandomSkyConditions() {
		int i = rnd.nextInt(skyConditions.length - 1);
		return skyConditions[i];
	}

	private String convertStreamToString(InputStream is) {
		/*
		 * To convert the InputStream to String we use the
		 * BufferedReader.readLine() method. We iterate until the BufferedReader
		 * return null which means there's no more data to read. Each line will
		 * appended to a StringBuilder and returned as String.
		 */
		BufferedReader reader = new BufferedReader(new InputStreamReader(is));
		StringBuilder sb = new StringBuilder();

		String line = null;
		try {
			while ((line = reader.readLine()) != null) {
				sb.append(line + "\n");
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				is.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		return sb.toString();
	}
}
