package org.easysoa.discovery.rest.model;

import java.net.URL;

public class Deliverable extends SoaNode {
    
    private URL location;

    public Deliverable() {}
    
    public Deliverable(String id, String name, String version, URL location) {
        super(id, name, version);
        this.location = location;
    }

    public URL getLocation() {
        return location;
    }
    
    @Override
    public SoaNodeType getSoaNodeType() {
        return SoaNodeType.Deliverable;
    }
    
}
