package org.easysoa.registry.rest.marshalling;

import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;

import org.easysoa.registry.SoaNodeId;

public class JsonDocumentMarshalling implements SoaNodeMarshalling {

    private static JsonConfig config;

    static {
        config = new JsonConfig();
        config.registerJsonBeanProcessor(GregorianCalendar.class, new CalendarJsonBeanProcessor());
    }
    
    @Override
    public String marshall(SoaNodeInformation soaNodeInfo) {
        return toJSON(soaNodeInfo).toString(2);
    }

    @Override
    public String marshall(List<SoaNodeInformation> soaNodeListInfo) {
        JSONArray soaNodeArray = new JSONArray();
        for (SoaNodeInformation soaNodeInfo : soaNodeListInfo) {
            soaNodeArray.add(toJSON(soaNodeInfo));
        }
        return soaNodeArray.toString(2);
    }
    
    private JSONObject toJSON(SoaNodeInformation soaNodeInfo) {
        JSONObject modelAsJSON = new JSONObject();
        modelAsJSON.put("doctype", soaNodeInfo.getId().getType());
        modelAsJSON.put("name", soaNodeInfo.getId().getName());
        
        Map<String, Object> propertyMap = soaNodeInfo.getProperties();
        if (propertyMap != null && !propertyMap.isEmpty()) {
            JSONObject properties = JSONObject.fromObject(soaNodeInfo.getProperties(), config);
            modelAsJSON.put("properties", properties);
        }
        
        return modelAsJSON;
    }

    @Override
    public String marshallError(String message, Exception e) {
        JSONObject result = new JSONObject();
        result.put("error", message + ": " + e.getMessage());
        result.put("stacktrace", e.getStackTrace());
        return result.toString();
    }

    @Override
    public SoaNodeInformation unmarshall(String string) {
        JSONObject json = JSONObject.fromObject(string);
        String doctype = (String) json.get("doctype");
        String name = (String) json.get("name");
        Map<String, Object> properties = toMap((JSONObject) json.get("properties"));
        return new SoaNodeInformation(new SoaNodeId(doctype, name), properties, null); // TODO correlated documents
    }
    
    private Map<String, Object> toMap(JSONObject jsonObject) {
       Map<String, Object> map = new HashMap<String, Object>();
       for (Object key : jsonObject.keySet()) {
           map.put((String) key, jsonObject.get(key));
       }
       return map;
    }
    
}
