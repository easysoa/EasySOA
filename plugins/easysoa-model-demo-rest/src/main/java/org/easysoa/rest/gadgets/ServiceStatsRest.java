package org.easysoa.rest.gadgets;

import static org.easysoa.doctypes.Service.PROP_CALLCOUNT;
import static org.easysoa.doctypes.Service.SCHEMA;

import javax.security.auth.login.LoginException;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.easysoa.rest.LoggedRest;
import org.json.JSONException;
import org.json.JSONObject;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentModelList;

@Path("easysoa/stats")
@Produces(MediaType.APPLICATION_JSON)
public class ServiceStatsRest extends LoggedRest {

	@GET
	public Object doGet() throws LoginException, ClientException, JSONException {
		
		login();
		
		JSONObject result = new JSONObject();
		DocumentModelList serviceList = session.query("SELECT * FROM Service WHERE serv:callcount > 0");
		for (DocumentModel serviceModel : serviceList) {
			JSONObject serviceInfo = new JSONObject();
			serviceInfo.put("id", serviceModel.getId());
			serviceInfo.put("title", serviceModel.getTitle());
			serviceInfo.put("callcount", serviceModel.getProperty(SCHEMA, PROP_CALLCOUNT)); 
			result.append("services", serviceInfo);
		}
		
		logout();
		
		return result.toString(2);
		
	}
	
}
