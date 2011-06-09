package com.openwide.easysoa.esperpoc;

import org.json.JSONException;
import org.json.JSONObject;

public interface JsonMapper {

	Object mapTo(JSONObject child) throws JSONException;
	
}
