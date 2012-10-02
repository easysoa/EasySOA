package org.easysoa.sca.intents.test;

import org.easysoa.test.mock.twittermock.TwitterMock;
import org.osoa.sca.annotations.Reference;
import net.webservicex.GlobalWeatherSoap;

public class IntentTestImpl implements IntentTest {

    @Reference
    GlobalWeatherSoap meteo_genService_ref;
    
    @Reference
    TwitterMock twitter_genService_ref;
    
    @Override
    public String getMeteoForecast(String city, String country) {
        return meteo_genService_ref.getWeather(city, country);
    }

    @Override
    public String getCitiesByCountry(String country) {
        return meteo_genService_ref.getCitiesByCountry(country);
    }

    @Override
    public String returnLastTweet(String user) {
        return twitter_genService_ref.returnLastTweet(user);
    }
    
}
