package com.openwide.easysoa.esperpoc.registration;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * JSON mapper to transform JSON object to generic object
 * @author jguillemotte
 *
 */
public interface JsonMapper {

	Object mapTo(JSONObject child) throws JSONException;
	
}
