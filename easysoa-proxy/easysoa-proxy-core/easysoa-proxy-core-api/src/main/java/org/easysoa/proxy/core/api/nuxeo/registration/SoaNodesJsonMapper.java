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

package org.easysoa.proxy.core.api.nuxeo.registration;

import org.apache.log4j.Logger;
import org.easysoa.proxy.core.api.monitoring.soa.Api;
import org.easysoa.proxy.core.api.monitoring.soa.Appli;
import org.easysoa.proxy.core.api.monitoring.soa.Node;
import org.easysoa.proxy.core.api.monitoring.soa.Service;
import org.json.JSONException;
import org.json.JSONObject;



/**
 * Maps JSON to ServiceAPI, Service, Workspace
 * else (ex.Folder) returns null
 * 
 * @author mdutoo
 *
 */
public class SoaNodesJsonMapper implements JsonMapper {
	
	@SuppressWarnings("unused")
    private static Logger logger = Logger.getLogger(Thread.currentThread().getStackTrace()[0].getClassName());

	@Override
	public Object mapTo(JSONObject child) throws JSONException {
		Node res = null;
		if("ServiceAPI".equals(child.get("type"))){
			String apiUrl = child.getJSONObject("properties").getString("api:url");
			int lastSlashIndex = apiUrl.lastIndexOf('/');
			String parentUrl;
			if (lastSlashIndex != -1) {
				parentUrl = apiUrl.substring(0, lastSlashIndex);
			} else {
				parentUrl = "http:"; // HACK TODO BETTER in nuxeo soa model
			}
			Api api = new Api(apiUrl, parentUrl);
			api.setTitle(child.getJSONObject("properties").getString("dc:title"));
			api.setDescription(child.getJSONObject("properties").getString("dc:description"));
			api.setApplication(child.getJSONObject("properties").getString("api:application"));
			api.setSourceUrl(child.getJSONObject("properties").getString("dc:source"));
			res = api;
		}
		else if("Service".equals(child.get("type"))){
			String serviceUrl = child.getJSONObject("properties").getString("serv:url");
			Service service = new Service(serviceUrl);
			service.setTitle(child.getString("title"));
			service.setDescription(child.getJSONObject("properties").getString("dc:title"));
			service.setCallCount(child.getJSONObject("properties").getInt("serv:callcount"));
			service.setContentTypeIn(child.getJSONObject("properties").getString("serv:contentTypeIn"));
			service.setContentTypeOut(child.getJSONObject("properties").getString("serv:contentTypeOut"));
			service.setHttpMethod(child.getJSONObject("properties").getString("serv:httpMethod"));
			service.setParticipants(child.getJSONObject("properties").getString("serv:relatedUsers"));
			res = service;
		}
		else if ("Workspace".equals(child.get("type"))){
			Appli appli = new Appli(child.getJSONObject("properties").getString("webc:name"), child.getJSONObject("properties").getString("app:url"));
			appli.setTitle(child.getJSONObject("properties").getString("dc:title"));
			appli.setDescription(child.getJSONObject("properties").getString("dc:description"));
			appli.setServer(child.getJSONObject("properties").getString("app:server"));
			appli.setSourcesUrl(child.getJSONObject("properties").getString("app:sourcesUrl"));
			appli.setStandard(child.getJSONObject("properties").getString("app:standard"));
			appli.setTechnology(child.getJSONObject("properties").getString("app:technology"));
			appli.setUiUrl(child.getJSONObject("properties").getString("app:uiUrl"));
			res = appli;
		}
		return res;
	}

}
