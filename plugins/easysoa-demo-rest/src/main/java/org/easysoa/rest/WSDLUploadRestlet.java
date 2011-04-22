package org.easysoa.rest;

import org.easysoa.descriptors.WSDLService;
import org.easysoa.treestructure.WorkspaceDeployer;
import org.jboss.logging.Logger;
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

/**
 * REST service to upload a WSDL to the Nuxeo repository.
 * Currently uploads blindly any given file.
 * 
 * Use:
 * .../nuxeo/restAPI/wsdlupload/{url}
 * Params:
 * {url} The file to upload, without the "http://" prefix (other protocols not supported), and not encoded
 * 
 * @author mkalam-alami
 *
 */
public class WSDLUploadRestlet extends BaseStatelessNuxeoRestlet {
	
	private static final Logger log = Logger.getLogger(WSDLUploadRestlet.class);
	private static final String REPOSITORY = "default";
	private static final String API_PATH = "wsdlupload/";

	public void handle(Request request, Response response) {
		super.initRepository(response, REPOSITORY);
		JSONObject result = new JSONObject();
		String failure = null;

		// URL Parsing
		String url = null;
		try {
			url = RequestURL.parse(request, API_PATH);
			result.append("url", url);
		} catch (Exception e) {
			failure = e.getMessage();
			e.printStackTrace();
		}

		// Basic file path testing / TODO
		if (!url.toLowerCase().contains("wsdl")) {
			failure = "Given URL doesn't seem to be a WSDL.";
		}
		
		// File download
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
				// Document creation
				DocumentModel model = this.session.createDocumentModel(
						WorkspaceDeployer.DESCRIPTORS_WORKSPACE + WSDLService.WSDL_DOCTYPE, IdUtils
								.generateStringId(), WSDLService.WSDL_DOCTYPE);
				model.setProperty("file", "content", f.getBlob());
				this.session.createDocument(model);
				this.session.save();
			} catch (ClientException e) {
				log.error("Failed to create WSDL", e);
				failure = e.getMessage();
			}
		}
 
		// Delete temporary file
		f.delete();
		
		// Format result
		String resultJSONP = null;
		try {
			result.append("error", failure);
			failure = null;
			resultJSONP = JSONP.format(result, request);
		} catch (Exception e) {
			log.warn("Cannot format JSONP message : " + e.getMessage());
		}
		
		// Send result
		try {
			if (failure != null)
				result.append("error", failure);
			
			if (resultJSONP != null)
				response.setEntity(new StringRepresentation(JSONP.format(result,
						request), MediaType.APPLICATION_JAVASCRIPT, Language.ALL,
						CharacterSet.UTF_8));
			else
				response.setEntity(new StringRepresentation(result.toString(2),
						MediaType.APPLICATION_JSON, Language.ALL,
						CharacterSet.UTF_8));
		} catch (Exception e) {
			log.warn("Cannot send message : " + e.getMessage());
		}
	}
}