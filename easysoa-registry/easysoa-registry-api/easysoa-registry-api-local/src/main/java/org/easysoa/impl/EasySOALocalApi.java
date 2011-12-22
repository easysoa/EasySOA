package org.easysoa.impl;

import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.easysoa.api.EasySOAApiSession;
import org.easysoa.api.EasySOADocument;
import org.easysoa.api.EasySOALocalDocument;
import org.easysoa.doctypes.AppliImpl;
import org.easysoa.doctypes.EasySOADoctype;
import org.easysoa.doctypes.Service;
import org.easysoa.doctypes.ServiceAPI;
import org.easysoa.doctypes.ServiceReference;
import org.easysoa.properties.ApiUrlProcessor;
import org.easysoa.services.DocumentService;
import org.easysoa.services.HttpDownloader;
import org.easysoa.services.HttpDownloaderService;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentModelList;
import org.nuxeo.ecm.core.api.IdRef;
import org.nuxeo.runtime.api.Framework;

/**
 * 
 * Local implementation of the EasySOA API, and core of the notification handling.
 * 
 * @author mkalam-alami
 *
 */
public class EasySOALocalApi implements EasySOAApiSession {
    
    private CoreSession session;
    
    private HttpDownloaderService httpDownloaderService;
    
    private static final Map<String, String> propertyFilter = new HashMap<String, String>();

    public EasySOALocalApi(CoreSession session) throws Exception {
        this.session = session;
    	this.httpDownloaderService = Framework.getService(HttpDownloaderService.class);
        
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
     * @return The created/updated Appli Impl. ID
     * @throws ClientException
     * @throws MalformedURLException 
     */
    public final EasySOADocument notifyAppliImpl(Map<String, String> properties)
            throws ClientException, MalformedURLException {
        
        // Check mandatory field
        String url = properties.get(Service.PROP_URL);
        if (url != null && !url.isEmpty()) {
            
            // Find or create document
            DocumentService docService = Framework.getRuntime().getService(DocumentService.class); 
            DocumentModel appliImplModel = docService.findAppliImpl(session, url);
            if (appliImplModel == null) {
                String title = (properties.get("title") != null) ? properties.get("title") : properties.get(AppliImpl.PROP_URL);
                String workspace = (properties.get(Service.PROP_ENVIRONMENT) != null) ? properties.get(Service.PROP_ENVIRONMENT) : "Master";
                appliImplModel = docService.createAppliImpl(session, url, workspace);
                appliImplModel.setProperty("dublincore", "title", title);
                appliImplModel = session.saveDocument(appliImplModel);
            }
            
            // Update optional properties
            setPropertiesIfNotNull(appliImplModel, AppliImpl.SCHEMA, AppliImpl.getPropertyList(), properties);
            setPropertiesIfNotNull(appliImplModel, AppliImpl.FEATURE_SCHEMA, AppliImpl.getFeaturePropertyList(), properties);
            
            // Save
            session.saveDocument(appliImplModel);
            session.save();

            return new EasySOALocalDocument(appliImplModel);
        
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
     * @return The created/updated API ID
     * @throws ClientException
     * @throws MalformedURLException 
     */
    public final EasySOADocument notifyServiceApi(Map<String, String> properties)
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
            DocumentModel parentModel = docService.findAppliImpl(session, parentUrl);
            if (parentModel == null) {
                parentModel = docService.findServiceApi(session, parentUrl);
            }
            if (parentModel == null) {
                if (parentUrl == null) {
                    parentModel = docService.getDefaultAppliImpl(session);
                }
                else {
                    String workspace = (properties.get(Service.PROP_ENVIRONMENT) != null) ? properties.get(Service.PROP_ENVIRONMENT) : "Master";
                    parentModel = docService.createAppliImpl(session, parentUrl, workspace);
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
                    HttpDownloader f = httpDownloaderService.createHttpDownloader(url);
                    f.download();
                    apiModel.setProperty("file", "content", f.getBlob());
                } catch (Exception e) {
                    throw new ClientException("Failed to download attached file", e);
                }
            }
            setPropertiesIfNotNull(apiModel, ServiceAPI.SCHEMA, ServiceAPI.getPropertyList(), properties);
            
            // Save
            apiModel = session.saveDocument(apiModel);
            session.save();

            return new EasySOALocalDocument(apiModel);
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
     * @return The created/updated Service ID
     * @throws ClientException
     * @throws MalformedURLException 
     */
    public final EasySOADocument notifyService(Map<String, String> properties) throws ClientException, MalformedURLException {
    
        // Check mandatory fields
        String url = properties.get(Service.PROP_URL);
        if (url != null && !url.isEmpty()) {

            // Exctract main fields
            String parentUrl = properties.get(Service.PROP_PARENTURL),
                title = properties.get(Service.PROP_TITLE);
            
            // Store URL as file in case of a WSDL
            if (url.toLowerCase().endsWith("?wsdl")) {
                properties.put(Service.PROP_FILEURL, url);
                url = url.substring(0, url.length() - 5);
            }
            
            if (parentUrl == null || parentUrl.isEmpty()) {
                parentUrl = ApiUrlProcessor.computeApiUrl(url);
            }
            if (title == null || title.isEmpty()) {
                title = ApiUrlProcessor.computeServiceTitle(url);
                properties.put(Service.PROP_TITLE, title);
            }
        
            // Find or create document and parent
            DocumentService docService = Framework.getRuntime().getService(DocumentService.class); 
            DocumentModel apiModel = docService.findServiceApi(session, parentUrl);
            if (apiModel == null) {
                // Guess Appli. Impl.
                String appliImplUrl = ApiUrlProcessor.computeAppliImplUrl(parentUrl);
                DocumentModel appliImplModel = docService.findAppliImpl(session, appliImplUrl.toString());
                if (appliImplModel == null) {
                    appliImplModel = docService.findAppliImpl(session, ApiUrlProcessor.computeAppliImplUrl(appliImplUrl));
                }
                if (appliImplModel == null) {
                    appliImplModel = docService.getDefaultAppliImpl(session, properties.get(Service.PROP_ENVIRONMENT));
                }
                // Create API
                apiModel = docService.createServiceAPI(session, appliImplModel.getPathAsString(), parentUrl);
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
            
            // Update location (unless the service has just been created)
            if (!apiModel.getRef().equals(serviceModel.getParentRef()) && serviceModel.getParentRef() != null) {
                serviceModel = session.move(serviceModel.getRef(), apiModel.getRef(), null);
            }
            
            // Save
            serviceModel = session.saveDocument(serviceModel); 
            session.save();

            return new EasySOALocalDocument(serviceModel);
            
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
    public final EasySOADocument notifyServiceReference(
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
                referenceModel = docService.createReference(session, appliImplModel.getPathAsString(), archiPath);
            }
            properties.remove(ServiceReference.PROP_PARENTURL);
            setPropertiesIfNotNull(referenceModel, ServiceReference.SCHEMA, ServiceReference.getPropertyList(), properties);
            
            referenceModel = session.saveDocument(referenceModel);
            session.save();

            return new EasySOALocalDocument(referenceModel);

        }
        else {
            throw new ClientException("Parent application URL or architecture path not informed");
        }
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
            }
        }
    }

    @Override
    public List<EasySOADocument> queryDocuments(String query) throws Exception {
        List<EasySOADocument> result = new LinkedList<EasySOADocument>();
        DocumentModelList list = session.query(query);
        for (DocumentModel model : list) {
            result.add(new EasySOALocalDocument(model));
        }
        return result;
    }

    @Override
    public EasySOADocument getDocumentById(String id) throws Exception {
        return new EasySOALocalDocument(session.getDocument(new IdRef(id)));
    }

}
