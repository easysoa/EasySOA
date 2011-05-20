package org.easysoa.rest;

import java.util.List;

import javax.security.auth.login.LoginContext;
import javax.security.auth.login.LoginException;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;

import org.easysoa.rest.tools.HttpFile;
import org.easysoa.rest.tools.JSONP;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.easysoa.descriptors.WSDLService;
import org.easysoa.services.ServiceListener;
import org.easysoa.tools.VocabularyService;
import org.easysoa.treestructure.WorkspaceDeployer;
import org.json.JSONObject;
import org.nuxeo.common.utils.IdUtils;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentModelList;
import org.nuxeo.ecm.webengine.WebEngine;
import org.nuxeo.runtime.api.Framework;
import org.restlet.data.CharacterSet;
import org.restlet.data.Language;
import org.restlet.data.MediaType;
import org.restlet.resource.StringRepresentation;

/**
 * REST service to upload a WSDL to the Nuxeo repository.
 * Currently uploads blindly any given file.
 * 
 * Use:
 * .../nuxeo/restAPI/wsdlupload/{applicationName}/{serviceName}/{url}
 * Params:
 * {applicationName} The application name, optional (ignored if empty) 
 * {serviceName} The service name, optional (ignored if empty)
 * {url} The file to upload, not encoded. Other protocols than HTTP are not supported.
 * 
 * @author mkalam-alami
 *
 */
@Path("easysoa/notification")
@Produces("application/x-javascript")
public class NotificationService {
	
	private static final Log log = LogFactory.getLog(NotificationService.class);

	@GET
	@Path("/{all:.*}")
	public Object doGet(@Context UriInfo uriInfo) {
		 
		// Parameters extraction
		
		List<String> callbacks = uriInfo.getQueryParameters().get("callback");
		String callback = null;
		if (callbacks != null) {
			callback = callbacks.get(0);
		}

		String query = uriInfo.getRequestUri().getQuery();
		if (query != null)
			query = query.replaceFirst("callback=[^&]*&?", "");
		
		String params = uriInfo.getRequestUri().getPath()
				.replaceAll(uriInfo.getBaseUri().getPath()+"easysoa/notification/", "");
		String[] paramArray = params.split("/");
		String url = "", applicationName = null, serviceName = null;
		if (paramArray.length >= 3) {
			if (!paramArray[0].isEmpty())
				applicationName = paramArray[0];
			if (!paramArray[1].isEmpty())
				serviceName = paramArray[1];
			url = params.replaceFirst(paramArray[0]+"/", "")
				.replaceFirst(paramArray[1]+"/", "")
				+ ((query != null) ? "?"+query : "") ;
		}
		
		if (url.isEmpty()) {
			return "Invalid use.";
		}
		
		// Initialization
		JSONObject result = new JSONObject();
		String errorMsg = null;
		
		CoreSession session = null;
		try {
			// As the REST API is anonymously available, we need to explicitly log in
			LoginContext ctx = Framework.login("Administrator", "Administrator");
			log.info("SIZE: "+ctx.getSubject().getPrincipals().size());
			session = WebEngine.getActiveContext().getUserSession().getCoreSession(null);
		} catch (LoginException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		// Basic file path testing / TODO
		if (!url.toLowerCase().contains("wsdl")) {
			errorMsg = "Given URL doesn't seem to be a WSDL.";
		}

		// File download
		HttpFile f = new HttpFile(url);
		if (errorMsg == null) {
			try {
				f.download();
			} catch (Exception e) {
				log.info("WSDL download problem : " + e.getMessage());
				errorMsg = e.getMessage();
			}
		}

		if (errorMsg == null) {
			
			DocumentModelList list = null;
			try {
				list = session.query("SELECT * FROM Document WHERE dc:title = '"+serviceName+"'");
			} catch (ClientException e) {
				log.error("Failed to query existing WSDLs", e);
			}
			
			// The WSDL is already online
			if (list != null && list.size() > 0) {
				errorMsg = "WSDL already registered.";
			}
			
			else {
				try {
					
					
					// WSDL creation
					DocumentModel model = session.createDocumentModel(
							WorkspaceDeployer.DESCRIPTORS_WORKSPACE + WSDLService.WSDL_DOCTYPE, IdUtils
									.generateStringId(), WSDLService.WSDL_DOCTYPE);
					model.setProperty("file", "content", f.getBlob());
					model = session.createDocument(model);
					
					// Service creation
					if (serviceName != null || applicationName != null) {
						
						DocumentModel serviceModel = session.createDocumentModel(
								WorkspaceDeployer.SERVICES_WORKSPACE,
								IdUtils.generateStringId(),
								ServiceListener.SERVICE_DOCTYPE);
						serviceModel.setProperty("dublincore", "title", serviceName);
						serviceModel.setProperty("serviceTags", "application", applicationName);
						serviceModel.setProperty("serviceTags", "descriptorid", model.getId());
						serviceModel = session.createDocument(serviceModel);
	
						model.setProperty("endpoints", "serviceid", serviceModel.getId());
						session.saveDocument(model);
						
						session.save();
	
						// New application
						if (applicationName != null
								&& !VocabularyService.entryExists(session, "application", applicationName)) {
							VocabularyService.addEntry(session, "application", applicationName, applicationName);
						}
					}
					
					session.save();
					
				} catch (ClientException e) {
					log.error("Failed to create WSDL", e);
					errorMsg = e.getMessage();
				}
			}
		}
 
		// Delete temporary file
		f.delete();
		
		// Format result
		String resultJSONP = null;
		try {
			result.put("result", (errorMsg != null) ? "ERROR: "+errorMsg : "ok");
			resultJSONP = JSONP.format(result, callback);
		} catch (Exception e) {
			log.warn("Cannot format JSONP message : " + e.getMessage());
		}
		
		// Send result
		try {
			if (resultJSONP != null)
				return new StringRepresentation(resultJSONP,
						MediaType.APPLICATION_JAVASCRIPT, Language.ALL,
						CharacterSet.UTF_8).getText();
			else
				return new StringRepresentation(result.toString(2),
						MediaType.APPLICATION_JSON, Language.ALL,
						CharacterSet.UTF_8).getText();
		} catch (Exception e) {
			log.warn("Cannot send message : " + e.getMessage());
		}
		
		return errorMsg;
	}
}