package org.easysoa.services;

import static org.easysoa.doctypes.AppliImpl.PROP_URL;
import static org.easysoa.doctypes.AppliImpl.SCHEMA;
import static org.easysoa.doctypes.Service.PROP_CALLCOUNT;
import static org.easysoa.doctypes.Service.PROP_PARENTURL;

import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.easysoa.doctypes.AppliImpl;
import org.easysoa.doctypes.EasySOADoctype;
import org.easysoa.doctypes.Service;
import org.easysoa.doctypes.ServiceAPI;
import org.easysoa.listeners.HttpFile;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.runtime.api.Framework;
import org.nuxeo.runtime.model.ComponentName;
import org.nuxeo.runtime.model.DefaultComponent;

public class NotificationService extends DefaultComponent {

    public static final ComponentName NAME = new ComponentName(
    		"org.easysoa.notification.NotificationServiceComponent");

    @SuppressWarnings("unused")
	private static final Log log = LogFactory.getLog(NotificationService.class);
    
	public void notifyAppliImpl(CoreSession session, Map<String, String> properties) throws Exception {
		
		// Check mandatory field
		if (properties.get(PROP_URL) != null) {
		
			try {

				DocumentService docService = Framework.getRuntime().getService(DocumentService.class); 
				
				// Find or create document
				DocumentModel appliImplModel = docService.findAppliImpl(session, properties.get(PROP_URL));
				if (appliImplModel == null) {
					String title = (properties.get("title") != null) ? properties.get("title") : properties.get(PROP_URL);
					appliImplModel = docService.createAppliImpl(session, properties.get(PROP_URL));
					appliImplModel.setProperty("dublincore", "title", title);
					session.saveDocument(appliImplModel);
				}
				
				// Update optional properties
				setPropertiesIfNotNull(appliImplModel, SCHEMA, AppliImpl.getPropertyList(), properties);
				
				// Save
				session.saveDocument(appliImplModel);
				session.save();
				
			} catch (ClientException e) {
				throw new Exception("Document creation failed: "+e.getMessage());
			}
		
		}
		else {
			throw new Exception("Appli name or root services URL not informed");
		}
	}

	public void notifyApi(CoreSession session, Map<String, String> params) throws Exception {
		
		// Check mandatory fields
		if (params.get(PROP_URL) != null) {
			
			// Exctract main fields
			String url = params.get(PROP_URL),
				parentUrl = params.get(PROP_PARENTURL),
				title = params.get("title");
			
			if (title == null)
				title = url;

			try {
				
				DocumentService docService = Framework.getRuntime().getService(DocumentService.class); 

				// Find or create document and parent
				DocumentModel parentModel = docService.findServiceApi(session, parentUrl);
				if (parentModel == null)
					parentModel = docService.findAppliImpl(session, parentUrl);
				if (parentModel == null) {
					if (parentUrl == null) {
						parentModel = docService.getDefaultAppliImpl(session);
					}
					else {
						parentModel = docService.createAppliImpl(session, parentUrl);
					}
					session.save();
				}
				
				DocumentModel apiModel = docService.findServiceApi(session, url);
				if (apiModel == null)
					apiModel = docService.createServiceAPI(session, parentModel.getPathAsString(), url);
				session.move(apiModel.getRef(), parentModel.getRef(), apiModel.getName());

				// Update optional properties
				if (url.toLowerCase().contains("wsdl")) {
					try {
						HttpFile f = new HttpFile(url);
						f.download();
						apiModel.setProperty("file", "content", f.getBlob());
					} catch (Exception e) {
						throw new Exception("Failed to download attached file");
					}
				}
				params.remove(PROP_PARENTURL);
				setPropertiesIfNotNull(apiModel, SCHEMA, ServiceAPI.getPropertyList(), params);
				
				// Save
				session.saveDocument(apiModel);
				session.save();
				
			} catch (ClientException e) {
				throw new Exception("Document creation failed: "+e.getMessage());
			}

		}
		else {
			throw new Exception("API URL or parent URL not informed");
		}
	}
	
	public void notifyService(CoreSession session, Map<String, String> properties) throws Exception {

		// Check mandatory fields
		if (properties.get(PROP_URL) != null && properties.get(PROP_PARENTURL) != null) {

			// Exctract main fields
			String url = properties.get(PROP_URL),
				parentUrl = properties.get(PROP_PARENTURL);
			int callcount;
			try {
				callcount = (properties.get(PROP_CALLCOUNT) != null) ? Integer.parseInt(properties.get(PROP_CALLCOUNT)) : 0;
			}
			catch (NumberFormatException e) {
				callcount = 0;
			}
			
			try {
				
				DocumentService docService = Framework.getRuntime().getService(DocumentService.class); 
				
				// Find or create document and parent
				DocumentModel apiModel = docService.findServiceApi(session, parentUrl);
				if (apiModel == null) {
					apiModel = docService.createServiceAPI(session, null, parentUrl); // TODO "by default", or even fail
					session.saveDocument(apiModel);
					session.save();
				}
				DocumentModel serviceModel = docService.findService(session, url);
				if (serviceModel == null)
					serviceModel = docService.createService(session, apiModel.getPathAsString(), url);
				session.move(serviceModel.getRef(), apiModel.getRef(), serviceModel.getName());

				// Update optional properties
				properties.remove(PROP_PARENTURL);
				setPropertiesIfNotNull(serviceModel, SCHEMA, Service.getPropertyList(), properties);
				try {
					serviceModel.setProperty(SCHEMA, PROP_CALLCOUNT, 
							((Integer) serviceModel.getProperty(SCHEMA, PROP_CALLCOUNT)) + callcount);
				}
				catch (Exception e) {
					serviceModel.setProperty(SCHEMA, PROP_CALLCOUNT, callcount);
				}
				
				// Save
				session.saveDocument(serviceModel);
				session.save();
				
			} catch (ClientException e) {
				throw new Exception("Document creation failed: "+e.getMessage());
			}

		}
		else {
			throw new Exception("Service URL or parent API URL not informed");
		}
		
	}

	/**
	 * Sets a property to a model, but only if the value parameter is not null.
	 * @param result
	 * @param callback
	 * @return
	 * @throws ClientException 
	 */
	private final void setPropertyIfNotNull(DocumentModel model, String schema, 
			String property, Object value) throws ClientException {
		if (value != null) {
			model.setProperty(schema, property, value);
		}
	}

	/**
	 * Sets properties of given schema to the specified model, but only if the value parameter is not null.
	 * If the property is not found in the given schema, it will try to find a match in SOA Common or Dublin Core property.
	 * @param result
	 * @param callback
	 * @return
	 * @throws ClientException 
	 */
	private final void setPropertiesIfNotNull(DocumentModel model, String schema, 
			Map<String, String> schemaDef, Map<String, String> properties) throws ClientException {
		
		// Update optional properties
		for (String key : properties.keySet()) {
			// Given schema specific properties
			if (schemaDef.containsKey(key)) {
				setPropertyIfNotNull(model, schema, key, properties.get(key));
			}
			// EasySOA specific properties
			else if (EasySOADoctype.getCommonPropertyList().containsKey(key)) {
				setPropertyIfNotNull(model, EasySOADoctype.SCHEMA_COMMON, key, properties.get(key));
			}
			// Dublin Core properties
			else if (EasySOADoctype.getDublinCorePropertyList().containsKey(key)) {
				setPropertyIfNotNull(model, "dublincore", key, properties.get(key));
			}
			// Unknown
			else {
				throw new ClientException("Unkown schema "+key);
			}
		}
	}
	
}
