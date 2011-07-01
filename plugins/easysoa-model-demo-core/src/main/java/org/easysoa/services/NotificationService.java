package org.easysoa.services;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.easysoa.doctypes.AppliImpl;
import org.easysoa.doctypes.EasySOADoctype;
import org.easysoa.doctypes.Service;
import org.easysoa.doctypes.ServiceAPI;
import org.easysoa.doctypes.ServiceReference;
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

	private static final String ERROR_API_URL_BASE = "Can't get service API url because ";
	private static final String ERROR_API_URL_APPLIIMPL = ERROR_API_URL_BASE + "bad appliimpl URL";
	private static final String ERROR_API_URL_API = ERROR_API_URL_BASE + "bad api URL";
	private static final String ERROR_API_URL_SERVICE = ERROR_API_URL_BASE + "bad service URL";
	
    /**
     * Creates or update an Appli Impl. given the specified properties.
     * Properties require at least application's URL (PROP_URL).
     * @param session
     * @param properties A set of properties of the document, among the AppliImpl.PROP_XXX constants.
	 * @return The created/updated Appli Impl.
     * @throws ClientException
     */
	public final DocumentModel notifyAppliImpl(CoreSession session, Map<String, String> properties) throws ClientException {
		
		// Check mandatory field
		if (properties.get(AppliImpl.PROP_URL) != null) {

			// Find or create document
			DocumentService docService = Framework.getRuntime().getService(DocumentService.class); 
			DocumentModel appliImplModel = docService.findAppliImpl(session, properties.get(AppliImpl.PROP_URL));
			if (appliImplModel == null) {
				String title = (properties.get("title") != null) ? properties.get("title") : properties.get(AppliImpl.PROP_URL);
				appliImplModel = docService.createAppliImpl(session, properties.get(AppliImpl.PROP_URL));
				appliImplModel.setProperty("dublincore", "title", title);
				session.saveDocument(appliImplModel);
			}
			
			// Update optional properties
			setPropertiesIfNotNull(appliImplModel, AppliImpl.SCHEMA, AppliImpl.getPropertyList(), properties);
			
			// Save
			session.saveDocument(appliImplModel);
			session.save();
			
			return appliImplModel;
		
		}
		else {
			throw new ClientException("Appli name or root services URL not informed");
		}
	}

	/**
     * Creates or update an API given the specified properties.
     * Properties require at least application's URL (PROP_URL) ;
     * the parent document URL (PROP_PARENTURL) is also recommended if known.
     * @param session
     * @param properties A set of properties of the document, among the ServiceAPI.PROP_XXX constants.
	 * @return The created/updated API
	 * @throws ClientException
	 */
	public final DocumentModel notifyApi(CoreSession session, Map<String, String> properties) throws ClientException {
		
		// Check mandatory fields
		if (properties.get(ServiceAPI.PROP_URL) != null) {
			
			// Exctract main fields
			String url = properties.get(ServiceAPI.PROP_URL),
				parentUrl = properties.get(ServiceAPI.PROP_PARENTURL),
				title = properties.get("title");
			
			if (title == null)
				title = url;
			
			// Find or create document and parent
			DocumentService docService = Framework.getRuntime().getService(DocumentService.class); 
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
			if (!parentModel.getRef().equals(apiModel.getParentRef())) {
				session.move(apiModel.getRef(), parentModel.getRef(), null);
			}

			// Update optional properties
			if (url.toLowerCase().contains("wsdl")) {
				try {
					HttpFile f = new HttpFile(url);
					f.download();
					apiModel.setProperty("file", "content", f.getBlob());
				} catch (Exception e) {
					throw new ClientException("Failed to download attached file");
				}
			}
			properties.remove(ServiceAPI.PROP_PARENTURL);
			setPropertiesIfNotNull(apiModel, ServiceAPI.SCHEMA, ServiceAPI.getPropertyList(), properties);
			
			// Save
			session.saveDocument(apiModel);
			session.save();
			
			return apiModel;
		}
		else {
			throw new ClientException("API URL or parent URL not informed");
		}
	}
	
	/**
     * Creates or update a Service given the specified properties.
     * Properties require at least application's URL (PROP_URL) and parent API URL (PROP_PARENTURL).
     * If parent API is unknown, you can use the {@link #computeApiUrl} function.
     * @param session
     * @param properties A set of properties of the document, among the Service.PROP_XXX constants.
	 * @return The created/updated Service
	 * @throws ClientException
	 */
	public final DocumentModel notifyService(CoreSession session,
			Map<String, String> properties) throws ClientException {

		// Check mandatory fields
		if (properties.get(Service.PROP_URL) != null && properties.get(Service.PROP_PARENTURL) != null) {

			// Exctract main fields
			String url = properties.get(Service.PROP_URL),
				parentUrl = properties.get(Service.PROP_PARENTURL);
		
			// Find or create document and parent
			DocumentService docService = Framework.getRuntime().getService(DocumentService.class); 
			DocumentModel apiModel = docService.findServiceApi(session, parentUrl);
			if (apiModel == null) {
				apiModel = docService.createServiceAPI(session, null, parentUrl); // TODO "by default", or even fail
				session.saveDocument(apiModel);
				session.save();
			}
			DocumentModel serviceModel = docService.findService(session, url);
			if (serviceModel == null)
				serviceModel = docService.createService(session, apiModel.getPathAsString(), url);
			if (!apiModel.getRef().equals(serviceModel.getParentRef())) {
				session.move(serviceModel.getRef(), apiModel.getRef(), null);
			}

			// Compute call count
			int newCallcount;
			try {
				newCallcount = Integer.parseInt((String) serviceModel.getProperty(Service.SCHEMA, Service.PROP_CALLCOUNT));
			}
			catch (NumberFormatException e) {
				newCallcount = 0;
			}
			try {
				newCallcount += (properties.get(Service.PROP_CALLCOUNT) != null) ? 
						Integer.parseInt(properties.get(Service.PROP_CALLCOUNT)) : 0;
			}
			catch (NumberFormatException e) {
				// Do nothing (invalid parameter)
			}
			properties.put(Service.PROP_CALLCOUNT, Integer.toString(newCallcount));
			
			// Update optional properties
			properties.remove(Service.PROP_PARENTURL);
			setPropertiesIfNotNull(serviceModel, Service.SCHEMA, Service.getPropertyList(), properties);
			
			// Save
			session.saveDocument(serviceModel);
			session.save();

			return serviceModel;
			
		}
		else {
			throw new ClientException("Service URL or parent API URL not informed");
		}
		
	}

	/**
     * Creates or update a ServiceReference given the specified properties.
     * Properties require at least an architecture path (PROP_ARCHIPATH).
     * @param session
     * @param properties A set of properties of the document, among the ServiceReference.PROP_XXX constants.
	 * @return The created/updated Service
	 * @throws ClientException
	 */
	public final DocumentModel notifyServiceReference(CoreSession session,
			Map<String, String> properties) throws ClientException {

		String archiPath = properties.get(ServiceReference.PROP_ARCHIPATH);
		String parentUrl = properties.get(ServiceReference.PROP_PARENTURL);
		
		if (archiPath != null && parentUrl != null) {
			
			// Find parent application
			DocumentService docService = Framework.getRuntime().getService(DocumentService.class);
			DocumentModel appliImplModel = docService.findAppliImpl(session, parentUrl);
			if (appliImplModel == null) {
				throw new ClientException("Parent application URL invalid");
			}
			
			// Find reference, then enrich or create
			DocumentModel referenceModel = docService.findReference(session, archiPath);
			if (referenceModel == null){
				referenceModel = docService.createReference(session, 
						appliImplModel.getPathAsString(), archiPath);
			}
			properties.remove(ServiceReference.PROP_PARENTURL);
			setPropertiesIfNotNull(referenceModel, ServiceReference.SCHEMA, 
					ServiceReference.getPropertyList(), properties);
			
			session.saveDocument(referenceModel);
			session.save();
			
			return referenceModel;

		}
		else {
			throw new ClientException("Parent application URL not informed");
		}
	}
	
	/**
	 * Guesses an API url given the service URL and others.
	 * Normalizes URLs first.
	 * @param appliImplUrl has to be empty (means no default root url for this appliimpl)
	 * or a well-formed URL.
	 * @param apiUrlPath
	 * @param serviceUrlPath
	 * @return
	 * @throws MalformedURLException 
	 */
	public final String computeApiUrl(String appliImplUrl, String apiUrlPath,
			String serviceUrlPath) throws MalformedURLException {
		apiUrlPath = normalizeUrl(apiUrlPath, ERROR_API_URL_API);
		serviceUrlPath = normalizeUrl(serviceUrlPath, ERROR_API_URL_SERVICE);
		
		int apiPathEndIndex = -1;
		
		if (appliImplUrl.length() != 0) {
			// appliImplUrl has to be well-formed
			appliImplUrl = normalizeUrl(appliImplUrl, ERROR_API_URL_APPLIIMPL);
			String defaultApiUrl = concatUrlPath(appliImplUrl, apiUrlPath);
			if (serviceUrlPath.contains(defaultApiUrl)) {
				apiPathEndIndex = serviceUrlPath.indexOf(defaultApiUrl) + defaultApiUrl.length();
			} // else default appliImplUrl does not apply
		} // else empty appliImplUrl means no default appliImplUrl for apis
		
		if (apiPathEndIndex == -1) {
			apiPathEndIndex = serviceUrlPath.lastIndexOf('/');
		}
		
		return normalizeUrl(serviceUrlPath.substring(0, apiPathEndIndex), ERROR_API_URL_API); // TODO http://localhost:9000/hrestSoapProxyWSIntern
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
				throw new ClientException("Unkown property "+key);
			}
		}
	}

	/**
	 * NB. no normalization done
	 * @param url
	 * @param urlPath
	 * @return
	 */
	private final String concatUrlPath(String ... urlPath) {
		StringBuffer sbuf = new StringBuffer();
		for (String urlPathElement : urlPath) {
			if (urlPath != null && urlPath.length != 0) {
				sbuf.append(urlPathElement);
				sbuf.append('/');
			}
		}
		if (sbuf.length() != 0) {
			sbuf.deleteCharAt(sbuf.length() - 1);
		}
		return sbuf.toString();
	}

	/**
	 * Normalizes the given URL :
	 * ensures all pathElements are separated by a single /
	 * AND IF IT CONTAINS "://" that it is OK according to java.net.URL
	 * @param stringUrl
	 * @param errMsg
	 * @return
	 * @throws MalformedURLException
	 */
	private final String normalizeUrl(String stringUrl, String errMsg) throws MalformedURLException {
		if (stringUrl == null) {
			throw new MalformedURLException(errMsg + " : " + stringUrl);
		}
		if (stringUrl.indexOf("://") != -1) {
			URL url = new URL(stringUrl);
			stringUrl = url.toString();
			return normalizeUrlPath(url.toString(), errMsg);
		}
		return concatUrlPath(stringUrl.split("/")); // if URL OK, remove the end '/' if any
	}

	/**
	 * Normalizes the given URL path : ensures all pathElements are separated by a single /
	 * @param stringUrl
	 * @param errMsg
	 * @return
	 * @throws MalformedURLException
	 */
	private final String normalizeUrlPath(String stringUrl, String errMsg) throws MalformedURLException {
		if (stringUrl == null) {
			throw new MalformedURLException(errMsg + " : " + stringUrl);
		}
		return concatUrlPath(stringUrl.split("/"));
	}
	
}
