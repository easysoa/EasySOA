/**
 * EasySOA Proxy
 * Copyright 2011 Open Wide
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * 
 * Contact : easysoa-dev@googlegroups.com
 */

package org.easysoa.scaffolding.wsdltemplate;

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
