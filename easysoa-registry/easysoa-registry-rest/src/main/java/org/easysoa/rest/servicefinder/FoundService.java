package org.easysoa.rest.servicefinder;

public class FoundService {

    private String name;
    
    private String url;
    
    private String applicationName;

    public FoundService(String name, String url, String applicationName) {
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
