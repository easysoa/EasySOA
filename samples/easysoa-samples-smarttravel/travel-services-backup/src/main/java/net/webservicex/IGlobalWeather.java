package net.webservicex;


public interface IGlobalWeather {
	public String getCitiesByCountry(String countryName);

	public String getWeather(String cityName, String countryName);
}
