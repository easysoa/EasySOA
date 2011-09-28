package org.easysoa.rest.scraping;

public class FoundService {

    private String name;
    
    private String url;
    
    private String applicationName;

    public FoundService(String applicationName, String name, String url) {
        this(name, url);
        this.applicationName = applicationName;
    }
    
    public FoundService(String name, String url) {
        this.name = name;
        this.url = url;
    }
    
    public String getName() {
        return name;
    }
    
    public String getURL() {
        return url;
    }
    
    public String getApplicationName() {
        return applicationName;
    }

}
