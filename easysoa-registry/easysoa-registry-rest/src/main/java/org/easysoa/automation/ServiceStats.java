package org.easysoa.automation;

import static org.easysoa.doctypes.Service.PROP_CALLCOUNT;
import static org.easysoa.doctypes.Service.SCHEMA;

import javax.ws.rs.core.Context;

import org.json.JSONException;
import org.json.JSONObject;
import org.nuxeo.ecm.automation.core.Constants;
import org.nuxeo.ecm.automation.core.annotations.Operation;
import org.nuxeo.ecm.automation.core.annotations.OperationMethod;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentModelList;

/***
 * Provides an overview of the services callcounts (for the 'servicestats' gadget).
 * @author mkalam-alami
 */
@Operation(id = ServiceStats.ID, category = Constants.CAT_SERVICES, label = "Services Stats", description = "Provides an overview of the services callcounts.")
public class ServiceStats {
    
    public final static String ID = "ServiceStats";

    @Context
    protected CoreSession session;

    @OperationMethod
	public String run() throws JSONException, ClientException {
		JSONObject result = new JSONObject();
		DocumentModelList serviceList = session.query("SELECT * FROM Service WHERE serv:callcount > 0");
		for (DocumentModel serviceModel : serviceList) {
			JSONObject serviceInfo = new JSONObject();
			serviceInfo.put("id", serviceModel.getId());
			serviceInfo.put("title", serviceModel.getTitle());
			serviceInfo.put("callcount", serviceModel.getProperty(SCHEMA, PROP_CALLCOUNT)); 
			result.append("services", serviceInfo);
		}
		return result.toString(2);
	}
	
}
