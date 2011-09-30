package org.easysoa.services;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.easysoa.doctypes.AppliImpl;
import org.easysoa.doctypes.EasySOADoctype;
import org.easysoa.doctypes.PropertyNormalizer;
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

    private static final String ERROR_API_URL_BASE = "Can't get service API url because ";
    private static final String ERROR_API_URL_APPLIIMPL = ERROR_API_URL_BASE + "bad appliimpl URL";
    private static final String ERROR_API_URL_API = ERROR_API_URL_BASE + "bad api URL";
    private static final String ERROR_API_URL_SERVICE = ERROR_API_URL_BASE + "bad service URL";
    
    private static final Map<String, String> propertyFilter = new HashMap<String, String>();

    private static Log log = LogFactory.getLog(NotificationService.class);
    
    public NotificationService() {
        propertyFilter.put(AppliImpl.PROP_URL, null);
        
        propertyFilter.put(ServiceAPI.PROP_URL, null);
        propertyFilter.put(ServiceAPI.PROP_PARENTURL, null);
        
        propertyFilter.put(Service.PROP_URL, null);
        propertyFilter.put(Service.PROP_PARENTURL, null);
    }
    
    /**
     * Creates or update an Appli Impl. given the specified properties.
     * Properties require at least application's URL (PROP_URL).
     * @param session
     * @param properties A set of properties of the document, among the AppliImpl.PROP_XXX constants.
     * @return The created/updated Appli Impl.
     * @throws ClientException
     * @throws MalformedURLException 
     */
    public final DocumentModel notifyAppliImpl(CoreSession session, Map<String, String> properties)
            throws ClientException, MalformedURLException {
        
        // Check mandatory field
        String url = properties.get(Service.PROP_URL);
        if (url != null && !url.isEmpty()) {
            
            // Find or create document
            DocumentService docService = Framework.getRuntime().getService(DocumentService.class); 
            DocumentModel appliImplModel = docService.findAppliImpl(session, url);
            if (appliImplModel == null) {
                String title = (properties.get("title") != null) ? properties.get("title") : properties.get(AppliImpl.PROP_URL);
                appliImplModel = docService.createAppliImpl(session, url);
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
     * @throws MalformedURLException 
     */
    public final DocumentModel notifyServiceApi(CoreSession session, Map<String, String> properties)
            throws ClientException, MalformedURLException {
        
        // Check mandatory fields
        String url = properties.get(Service.PROP_URL);
        if (url != null && !url.isEmpty()) {
            
            // Exctract main fields
            String parentUrl = properties.get(ServiceAPI.PROP_PARENTURL),
                title = properties.get("title");
            
            if (title == null || title.isEmpty()) {
                title = url;
                properties.put("title", title);
            }
            
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
            if (apiModel == null) {
                apiModel = docService.createServiceAPI(session, parentModel.getPathAsString(), url);
            }
            if (!parentModel.getRef().equals(apiModel.getParentRef())) {
                apiModel = session.move(apiModel.getRef(), parentModel.getRef(), null);
            }

            // Update optional properties
            if (url.toLowerCase().contains("wsdl")) {
                try {
                    HttpFile f = new HttpFile(new URL(url));
                    f.download();
                    apiModel.setProperty("file", "content", f.getBlob());
                } catch (Exception e) {
                    throw new ClientException("Failed to download attached file", e);
                }
            }
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
     * @throws MalformedURLException 
     */
    public final DocumentModel notifyService(CoreSession session,
            Map<String, String> properties) throws ClientException, MalformedURLException {
        
        // Check mandatory fields
        String url = properties.get(Service.PROP_URL);
        if (url != null && !url.isEmpty()) {

            // Exctract main fields
            String parentUrl = properties.get(Service.PROP_PARENTURL),
                title = properties.get(Service.PROP_TITLE);
            
            // Store URL as file in case of a WSDL
            if (url.toLowerCase().contains("wsdl")) {
                properties.put(Service.PROP_FILEURL, url);
            }
            
            if (parentUrl == null || parentUrl.isEmpty()) {
                parentUrl = computeApiUrl(url);
            }
            if (title == null || title.isEmpty()) {
                title = computeServiceTitle(url);
                properties.put(Service.PROP_TITLE, title);
            }
        
            // Find or create document and parent
            DocumentService docService = Framework.getRuntime().getService(DocumentService.class); 
            DocumentModel apiModel = docService.findServiceApi(session, parentUrl);
            if (apiModel == null) {
                // Guess Appli. Impl.
                String appliImplUrl = computeAppliImplUrl(parentUrl);
                DocumentModel appliImplModel = docService.findAppliImpl(session, appliImplUrl.toString());
                if (appliImplModel == null) {
                    appliImplModel = docService.findAppliImpl(session, computeAppliImplUrl(appliImplUrl));
                }
                // Create API
                apiModel = docService.createServiceAPI(session, 
                        (appliImplModel != null) ? appliImplModel.getPathAsString() : null, parentUrl);
                apiModel.setProperty("dublincore", "title", title+" API");
                session.saveDocument(apiModel);
                session.save();
            }
            DocumentModel serviceModel = docService.findService(session, url);
            if (serviceModel == null) {
                serviceModel = docService.createService(session, apiModel.getPathAsString(), url);
            }

            // Update optional properties
            properties.put(Service.PROP_CALLCOUNT, 
                    getNewCallcount(serviceModel, properties.get(Service.PROP_CALLCOUNT))
                );
            setPropertiesIfNotNull(serviceModel, Service.SCHEMA, Service.getPropertyList(), properties);
            
            // Update location
            if (!apiModel.getRef().equals(serviceModel.getParentRef())) {
                serviceModel = session.move(serviceModel.getRef(), apiModel.getRef(), null);
            }
            
            // Save
            session.saveDocument(serviceModel); 
            session.save();

            return serviceModel;
            
        }
        else {
            throw new ClientException("Service URL not informed");
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
            DocumentModel referenceModel = docService.findServiceReference(session, archiPath);
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
            throw new ClientException("Parent application URL or architecture path not informed");
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
        
        apiUrlPath = PropertyNormalizer.normalizeUrl(apiUrlPath, ERROR_API_URL_API);
        serviceUrlPath = PropertyNormalizer.normalizeUrl(serviceUrlPath, ERROR_API_URL_SERVICE);
        
        int apiPathEndIndex = -1;
        
        try {
            if (appliImplUrl.length() != 0) {
                // appliImplUrl has to be well-formed
                appliImplUrl = PropertyNormalizer.normalizeUrl(appliImplUrl, ERROR_API_URL_APPLIIMPL);
                String defaultApiUrl = PropertyNormalizer.concatUrlPath(appliImplUrl, apiUrlPath);
                if (serviceUrlPath.contains(defaultApiUrl)) {
                    apiPathEndIndex = serviceUrlPath.indexOf(defaultApiUrl) + defaultApiUrl.length();
                } // else default appliImplUrl does not apply
            } // else empty appliImplUrl means no default appliImplUrl for apis
        }
        catch (Exception e) {
            log.warn("Failed to compute API url from appli URL & API URL path, using default ("+e.getMessage()+")");
        }
        
        if (apiPathEndIndex == -1) {
            return computeApiUrl(serviceUrlPath);
        }
        
        return PropertyNormalizer.normalizeUrl(
                serviceUrlPath.substring(0, apiPathEndIndex), ERROR_API_URL_API); // TODO http://localhost:9000/hrestSoapProxyWSIntern
    }
    
    public String computeApiUrl(String serviceUrlPath) throws MalformedURLException {
        return PropertyNormalizer.normalizeUrl(
                serviceUrlPath.substring(0, serviceUrlPath.lastIndexOf('/')),
                ERROR_API_URL_API); 
    }
    
    public String computeAppliImplUrl(String apiUrlPath) throws MalformedURLException {
        if (apiUrlPath.replace("://", "").lastIndexOf('/') != -1) {
            return PropertyNormalizer.normalizeUrl(
                    apiUrlPath.substring(0, apiUrlPath.lastIndexOf('/')),
                    ERROR_API_URL_APPLIIMPL); 
        }
        else {
            URL url = new URL(apiUrlPath);
            return PropertyNormalizer.normalizeUrl(url.getProtocol()+"://"+url.getHost());
        }
    }

    public String computeServiceTitle(String serviceUrlPath) throws MalformedURLException {
        String lastUrlPart = serviceUrlPath.substring(serviceUrlPath.lastIndexOf('/')+1);
        return lastUrlPart.replaceAll("(wsdl|WSDL|[^\\w.-])", "");
    }
    
    private String getNewCallcount(DocumentModel serviceModel, String newCalls) {
        Long previousCallcount, newCallsLong;
        try {
            previousCallcount = (Long) serviceModel.getProperty(Service.SCHEMA, Service.PROP_CALLCOUNT);
        } catch (Exception e) {
            previousCallcount = 0L;
        }
        if (previousCallcount == null) {
            previousCallcount = 0L;
        }
        try {
            newCallsLong = Long.parseLong(newCalls);
        } catch (Exception e) {
            newCallsLong = 0L;
        }
        return ((Long) (newCallsLong + previousCallcount)).toString();
    }
    
    /**
     * Sets a property to a model, but only if the value parameter is not null.
     * @param result
     * @param callback
     * @return
     * @throws ClientException 
     */
    private void setPropertyIfNotNull(DocumentModel model, String schema, 
            String property, Object value) throws ClientException {
        if (value != null && !propertyFilter.containsKey(property)) {
            // Append value if the property is a discovery field
            if (property.equals(EasySOADoctype.PROP_DTBROWSING)
                    || property.equals(EasySOADoctype.PROP_DTIMPORT)
                    || property.equals(EasySOADoctype.PROP_DTMONITORING)) {
                String prevValue = (String) model.getProperty(schema, property);
                if (prevValue == null) {
                    model.setProperty(schema, property, value);
                }
                else if (!prevValue.contains(value.toString())) {
                    model.setProperty(schema, property, prevValue + ", " + value);
                }
            }
            // Otherwise just set the property as expected
            else {
                model.setProperty(schema, property, value);
            }
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
    private void setPropertiesIfNotNull(DocumentModel model, String schema, 
            Map<String, String> schemaDef, Map<String, String> properties) throws ClientException {
        
        properties.putAll(propertyFilter);
        
        // Update optional properties
        for (Entry<String, String> entry : properties.entrySet()) {
            
            String key = entry.getKey();
            String value = entry.getValue();
            
            if (value != null && !value.isEmpty()) {
                // Given schema specific properties
                if (schemaDef.containsKey(key)) {
                    setPropertyIfNotNull(model, schema, key, value);
                }
                // EasySOA specific properties
                else if (EasySOADoctype.getCommonPropertyList().containsKey(key)) {
                    setPropertyIfNotNull(model, EasySOADoctype.SCHEMA_COMMON, key, value);
                }
                // Dublin Core properties
                else if (EasySOADoctype.getDublinCorePropertyList().containsKey(key)) {
                    setPropertyIfNotNull(model, "dublincore", key, value);
                }
                // Unknown
                else {
                    throw new ClientException("Unkown property "+key);
                }
            }
        }
    }
    
}
