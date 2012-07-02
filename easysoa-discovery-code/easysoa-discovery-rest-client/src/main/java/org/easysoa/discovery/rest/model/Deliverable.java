package org.easysoa.discovery.rest.model;

import java.net.URL;

public class Deliverable extends SOANode {
    
    private URL location;
    
    public Deliverable(String id, String name, String version, URL location) {
        super(id, name, version);
        this.location = location;
    }

    public URL getLocation() {
        return location;
    }

}
