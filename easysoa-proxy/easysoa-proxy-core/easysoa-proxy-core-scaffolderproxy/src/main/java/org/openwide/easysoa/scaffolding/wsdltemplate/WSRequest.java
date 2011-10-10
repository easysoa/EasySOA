package org.openwide.easysoa.scaffolding.wsdltemplate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * 
 * @author jguillemotte
 *
 */
public class WSRequest {

	private String service;
	private String binding;
	private String operation;
	private String wsdlUrl;
	private HashMap<String, List<String>> paramList;
	
	public WSRequest(){
		this.service = "";
		this.binding = "";
		this.operation = "";
		this.wsdlUrl = "";
		this.paramList = new HashMap<String, List<String>>();
	}
	
	public String getService() {
		return service;
	}
	
	public void setService(String service) {
		this.service = service;
	}
	
	public String getBinding() {
		return binding;
	}
	
	public void setBinding(String binding) {
		this.binding = binding;
	}
	public String getOperation() {
		return operation;
	}
	public void setOperation(String operation) {
		this.operation = operation;
	}
	
	public String getWsdlUrl() {
		return wsdlUrl;
	}
	
	public void setWsdlUrl(String wsdlUrl) {
		this.wsdlUrl = wsdlUrl;
	}

	public HashMap<String, List<String>> getParamList() {
		return paramList;
	}

	public void setParamList(HashMap<String, List<String>> paramList) {
		this.paramList = paramList;
	}

	public String toString(){
		return "wsRequest instance : wsdlUrl = " + wsdlUrl; 
	}
	
	/**
	 * Parse a JSON expression and transform it to a WSRequest object
	 * @param jsonString
	 * @return
	 * @throws JSONException
	 */
	public static final WSRequest parseJSON(String jsonString) throws JSONException{
		JSONObject jsonParametersObject = new JSONObject(jsonString);
		JSONObject jsonRequest = jsonParametersObject.getJSONObject("wsRequest");
		WSRequest request = new WSRequest();
		request.service = jsonRequest.getString("service");
		request.binding = jsonRequest.getString("binding");
		request.operation = jsonRequest.getString("operation");
		request.wsdlUrl = jsonRequest.getString("wsdlUrl");
		JSONArray paramsArray =	jsonParametersObject.getJSONArray("formParameters");
		for(int i=0; i<paramsArray.length(); i++){
			if(!request.getParamList().containsKey(paramsArray.getJSONObject(i).getString("paramName"))){
				List<String> valueList = new ArrayList<String>();
				valueList.add(paramsArray.getJSONObject(i).getString("paramValue"));
				request.getParamList().put(paramsArray.getJSONObject(i).getString("paramName"), valueList);
			} else {
				request.getParamList().get(paramsArray.getJSONObject(i).getString("paramName")).add(paramsArray.getJSONObject(i).getString("paramValue"));
			}
		}
		return request;
	}
	
}
