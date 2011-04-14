package org.easysoa.rest;

import org.jboss.logging.Logger;
import org.json.JSONException;
import org.json.JSONObject;
import org.nuxeo.common.utils.IdUtils;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.platform.ui.web.restAPI.BaseStatelessNuxeoRestlet;
import org.restlet.data.CharacterSet;
import org.restlet.data.Language;
import org.restlet.data.MediaType;
import org.restlet.data.Request;
import org.restlet.data.Response;
import org.restlet.resource.StringRepresentation;

public class WSDLUploadRestlet extends BaseStatelessNuxeoRestlet {
	
	private static final Logger log = Logger.getLogger(WSDLUploadRestlet.class);
	private static final String REPOSITORY = "default";

	public void handle(Request request, Response response) {
		super.initRepository(response, REPOSITORY);
		JSONObject result = new JSONObject();
		String failure = null;

		String url = null;
		try {
			url = RequestURL.parse(request);
			result.append("url", url);
		} catch (Exception e) {
			failure = e.getMessage();
			e.printStackTrace();
		}

		if (!url.endsWith("wsdl")) {
			failure = "Given URL doesn't seem to be a WSDL.";
		}

		HttpFile f = new HttpFile(url);
		if (failure == null) {
			try {
				f.download();
			} catch (Exception e) {
				log.info("WSDL download problem : " + e.getMessage());
				failure = e.getMessage();
			}
		}

		if (failure == null) {
			try {
				DocumentModel model = this.session.createDocumentModel(
						"/default-domain/workspaces/Descripteurs/WSDL", IdUtils
								.generateStringId(), "WSDL");

				model.setProperty("file", "content", f.getBlob());
				this.session.createDocument(model);
				this.session.save();

				response.setEntity(new StringRepresentation(result.toString(),
						MediaType.APPLICATION_JSON, Language.ALL,
						CharacterSet.UTF_8));
			} catch (ClientException e) {
				log.error("Failed to create WSDL", e);
				failure = e.getMessage();
			}
		}

		f.delete();
		try {
			result.append("error", failure);
			if (failure != null)
				response.setEntity(new StringRepresentation(JSONP.format(
						result, request), MediaType.APPLICATION_JAVASCRIPT,
						Language.ALL, CharacterSet.UTF_8));
		} catch (JSONException e) {
			log.warn("Cannot send error message : " + e.getMessage());
		}
	}
}