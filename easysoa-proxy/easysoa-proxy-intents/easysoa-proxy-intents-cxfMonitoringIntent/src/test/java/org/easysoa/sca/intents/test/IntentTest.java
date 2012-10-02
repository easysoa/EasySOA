package org.easysoa.sca.intents.test;

public interface IntentTest {

    public String getMeteoForecast(String city, String country);
    
    public String getCitiesByCountry(String country);
    
    public String returnLastTweet(String user);
}
