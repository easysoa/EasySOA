package org.easysoa.automation;

import javax.ws.rs.core.Context;

import org.json.JSONException;
import org.nuxeo.ecm.automation.core.Constants;
import org.nuxeo.ecm.automation.core.annotations.Operation;
import org.nuxeo.ecm.automation.core.annotations.OperationMethod;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
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
	public DocumentModelList run() throws JSONException, ClientException {
		return session.query("SELECT * FROM Service WHERE serv:callcount > 0");
	}
	
}
