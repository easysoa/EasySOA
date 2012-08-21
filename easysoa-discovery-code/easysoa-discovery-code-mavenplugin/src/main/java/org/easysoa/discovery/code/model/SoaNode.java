package org.easysoa.discovery.code.model;

import java.util.HashMap;
import java.util.Map;

import net.sf.json.JSONObject;

public class SoaNode {

    private String doctype;
    private String name;
    private Map<String, Object> properties = new HashMap<String, Object>();

    public SoaNode(String doctype, String name) {
        this.doctype = doctype;
        this.name = name;
    }
    
    public void setProperty(String xpath, Object value) {
        this.properties.put(xpath, value);
    }
    
    public void setTitle(String title) {
        this.setProperty("dc:title", title);
    }
    
    public String getType() {
        return this.doctype;
    }

    public String getName() {
        return name;
    }

    public String toJSONString() {
        JSONObject json = new JSONObject();
        json.put("doctype", this.doctype);
        json.put("name", this.name);
        json.put("properties", this.properties);
        return json.toString();
    }
    
}
