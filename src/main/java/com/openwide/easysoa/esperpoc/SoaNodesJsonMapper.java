package com.openwide.easysoa.esperpoc;

import org.apache.log4j.Logger;
import org.json.JSONException;
import org.json.JSONObject;
import com.openwide.easysoa.monitoring.soa.Api;
import com.openwide.easysoa.monitoring.soa.Appli;
import com.openwide.easysoa.monitoring.soa.Node;
import com.openwide.easysoa.monitoring.soa.Service;

public class SoaNodesJsonMapper implements JsonMapper {
	
	private static Logger logger = Logger.getLogger(Thread.currentThread().getStackTrace()[0].getClassName());

	@Override
	public Object mapTo(JSONObject child) throws JSONException {
		Node res = null;
		if("ServiceAPI".equals(child.get("type"))){
			String apiUrl = child.getJSONObject("properties").getString("api:url");
			// getting parent url
			int lastSlashIndex = apiUrl.lastIndexOf('/');
			String parentUrl;
			if (lastSlashIndex != -1) {
				parentUrl = apiUrl.substring(0, lastSlashIndex);
			} else {
				parentUrl = "http:"; // HACK TODO BETTER in nuxeo soa model
			}
			Api api = new Api(apiUrl, parentUrl);
			api.setTitle(child.getJSONObject("properties").getString("dc:title"));
			res = api;
		}
		else if("Service".equals(child.get("type"))){
			String serviceUrl = child.getJSONObject("properties").getString("serv:url");
			// getting parent url
			int lastSlashIndex = serviceUrl.lastIndexOf('/');
			String parentUrl;
			if (lastSlashIndex != -1) {
				parentUrl = serviceUrl.substring(0, lastSlashIndex);
			} else {
				parentUrl = "http:"; // HACK TODO BETTER in nuxeo soa model
			}
			Service service = new Service(serviceUrl, parentUrl);
			service.setTitle(child.getJSONObject("properties").getString("dc:title"));
			res = service;
		}
		else if ("Workspace".equals(child.get("type"))){
			Appli appli = new Appli(child.getJSONObject("properties").getString("webc:name"), child.getJSONObject("properties").getString("app:rootServicesUrl"));
			appli.setTitle(child.getJSONObject("properties").getString("dc:title"));
			res = appli;
		}
		return res;
	}

}
