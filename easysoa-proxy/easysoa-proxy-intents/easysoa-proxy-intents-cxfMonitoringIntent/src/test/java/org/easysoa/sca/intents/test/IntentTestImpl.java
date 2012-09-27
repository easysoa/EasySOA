package org.easysoa.sca.intents.test;

import org.osoa.sca.annotations.Reference;
import net.webservicex.GlobalWeatherSoap;

public class IntentTestImpl implements IntentTest {

    @Reference
    GlobalWeatherSoap meteo_genService_ref;
    
    @Override
    public String getMeteoForecast(String city, String country) {
        return meteo_genService_ref.getWeather(city, country);
    }

    @Override
    public String getCitiesByCountry(String country) {
        return meteo_genService_ref.getCitiesByCountry(country);
    }

}
