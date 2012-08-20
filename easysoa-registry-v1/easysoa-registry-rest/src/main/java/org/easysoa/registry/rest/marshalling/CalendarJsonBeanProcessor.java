package org.easysoa.registry.rest.marshalling;

import java.util.Calendar;

import net.sf.json.JSONObject;
import net.sf.json.JsonConfig;
import net.sf.json.processors.JsonBeanProcessor;

public class CalendarJsonBeanProcessor implements JsonBeanProcessor {

    @Override
    public JSONObject processBean(Object object, JsonConfig config) {
        if (object instanceof Calendar) {
            Calendar calendar = (Calendar) object;
            JSONObject result =  new JSONObject();
            result.put("timestamp", calendar.getTimeInMillis());
            return result;
        }
        else {
            return null;
        }
    }

}
