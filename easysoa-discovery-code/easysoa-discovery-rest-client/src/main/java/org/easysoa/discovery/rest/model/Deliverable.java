package org.easysoa.discovery.rest.model;

import java.net.URL;

import org.codehaus.jackson.node.ObjectNode;

public class Deliverable extends SOANode {
    
    private URL location;
    
    public Deliverable(String id, String name, String version, URL location) {
        super(id, name, version);
        this.location = location;
    }

    public URL getLocation() {
        return location;
    }

    @Override
    public ObjectNode toJSON() {
        ObjectNode node = super.toJSON();
        node.put("location", this.location.toString());
        return node;
    }
    
    @Override
    public SOANodeType getSOANodeType() {
        return SOANodeType.DELIVERABLE;
    }
    
}
