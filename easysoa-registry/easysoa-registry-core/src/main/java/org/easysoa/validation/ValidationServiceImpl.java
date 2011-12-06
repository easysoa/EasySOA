package org.easysoa.validation;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.easysoa.doctypes.Service;
import org.easysoa.doctypes.Workspace;
import org.easysoa.services.DeletedDocumentFilter;
import org.easysoa.services.DocumentService;
import org.nuxeo.ecm.core.api.Blob;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.DocumentModelList;
import org.nuxeo.ecm.core.api.DocumentRef;
import org.nuxeo.ecm.core.api.IdRef;
import org.nuxeo.ecm.core.api.impl.DocumentModelListImpl;
import org.nuxeo.runtime.api.Framework;

/**
 * 
 * @author mkalam-alami, mdutoo
 * 
 */
public class ValidationServiceImpl implements ValidationService {

    private static final Log log = LogFactory.getLog(ValidationServiceImpl.class);

    @Override
    public void validateServices(CoreSession session, DocumentModel model) throws Exception {

        // Find model's workspace and reference environment
        DocumentService docService = Framework.getService(DocumentService.class);
        boolean modelIsWorkspace = Workspace.DOCTYPE.equals(model.getType());
        DocumentModel workspace = (modelIsWorkspace) ? model : docService.getWorkspace(session, model);
        
        DocumentModel referenceEnv = docService.findEnvironment(session, 
                (String) workspace.getProperty(Workspace.SCHEMA, Workspace.PROP_REFERENCEDENVIRONMENT));

        // Start validation
        DocumentModelList services = null;
        List<String> errors = new LinkedList<String>(), result;
        int errorsCount = 0;
        
        if (referenceEnv != null) {

            // Find services to match
            DocumentModelList referenceServices = getAllServices(session, referenceEnv);
            if (Service.DOCTYPE.equals(model.getType())) {
                services = new DocumentModelListImpl();
                services.add(model);
            }
            else {
                services = getAllServices(session, model);
            }
            
            // Validate services
            if (services != null) {
                for (DocumentModel service : services) {
                    DocumentModel matchingService = getMatchingService(session, service, referenceServices);
                    result = validateService(service, matchingService);
                    if (!result.isEmpty()) {
                        errorsCount += result.size();
                        errors.addAll(result);
                    }
                }
                
            }
        
        }
        else {
            errors.add("No valid reference environment");
        }

        // Save validation state and result log
        Boolean wasValidated = (Boolean) workspace.getProperty(Workspace.SCHEMA, Workspace.PROP_ISVALIDATED),
                isNowValidated = (errorsCount == 0);
        if (modelIsWorkspace || (wasValidated && !isNowValidated)) {
            workspace.setProperty(Workspace.SCHEMA, Workspace.PROP_ISVALIDATED, isNowValidated);
        }
        SimpleDateFormat dateFormat = new SimpleDateFormat();
        String validationLog = "Last validation run at " + dateFormat.format(new Date()) + '\n';
        if (isNowValidated) {
            validationLog += "Successfully validated " + services.size() + " services against environment "+ referenceEnv.getTitle();
            if (!modelIsWorkspace) {
                validationLog += "(partial workspace validation)";
            }
            validationLog += ".";
        }
        else {
            validationLog += "Found " + errorsCount + " validation errors.\n";
            for (String error : errors) {
                validationLog +=  " * " + error + '\n';
            }
        }
        workspace.setProperty(Workspace.SCHEMA, Workspace.PROP_VALIDATIONLOG, validationLog);
        session.saveDocument(workspace);
        session.save();
    
    }
    
    private DocumentModel getMatchingService(CoreSession session, DocumentModel service,
            DocumentModelList referenceServices) throws ClientException {
        
        DocumentModel referenceModel = null;
        
        // Fetch reference service
        String referenceId = (String) service.getProperty(Service.SCHEMA, Service.PROP_REFERENCESERVICE);
        if (referenceId != null) {
            DocumentRef referenceRef = new IdRef(referenceId);
            if (referenceRef != null) {
                referenceModel = session.getDocument(referenceRef);
            }
        }
        
        // Guess reference service by correlation on WSDLs
        if (referenceModel == null) {
            String namespace = (String) service.getProperty(Service.SCHEMA, Service.PROP_WSDLNAMESPACE);
            String serviceName = (String) service.getProperty(Service.SCHEMA, Service.PROP_WSDLSERVICENAME);
            if (namespace != null && !namespace.isEmpty() && serviceName != null && !serviceName.isEmpty()) {
                for (DocumentModel potentialMatch : referenceServices) {
                    if (namespace.equals(potentialMatch.getProperty(Service.SCHEMA, Service.PROP_WSDLNAMESPACE))
                            && namespace.equals(potentialMatch.getProperty(Service.SCHEMA, Service.PROP_WSDLSERVICENAME))) {
                        referenceModel = potentialMatch;
                        break;
                    }
                }
            }
        }
        
        return referenceModel;
    }
    

    /**
     * 
     * @param service
     * @param referenceService
     * @return A list of errors
     */
    private List<String> validateService(DocumentModel service, DocumentModel referenceService) {

        // Init
        List<String> errors = new LinkedList<String>();
        String serviceName = "???";
        try {
            serviceName = service.getTitle();
        } catch (ClientException e) {
            log.warn("Failed to fetch document title: " + e.getMessage());
        }
        
        // Run validation
        try {
            
            if (referenceService != null) {

                // Check WSDL
                // Note: delta-based validation would be : ex. if wsdl changed, check it for
                // now only full validation.
                Blob referenceEnvWsdlBlob = (Blob) referenceService.getProperty("file", "content");
                if (referenceEnvWsdlBlob != null) {
                    Blob wsdlBlob = (Blob) service.getProperty("file", "content");
                    File referenceEnvWsdlFile = File.createTempFile("easysoa", "wsdl");
                    File wsdlFile = File.createTempFile("easysoa", "wsdl");
                    referenceEnvWsdlBlob.transferTo(referenceEnvWsdlFile);
                    wsdlBlob.transferTo(wsdlFile);
                    if (wsdlBlob != null && !isFinerWsdlThan(wsdlFile, referenceEnvWsdlFile)) {
                        errors.add(serviceName + " : service has different wsdl");
                    }
                }

                // check discovery (either by browsing or by monitoring) TODO restrict to runtime env only
                String discoveryTypeBrowsing = (String) referenceService.getProperty(Service.SCHEMA_COMMON, Service.PROP_DTBROWSING);
                String discoveryTypeMonitoring = (String) referenceService.getProperty(Service.SCHEMA_COMMON, Service.PROP_DTMONITORING);
                if (discoveryTypeBrowsing == null || discoveryTypeBrowsing.isEmpty()
                        && (discoveryTypeMonitoring == null || discoveryTypeMonitoring.isEmpty())) {
                    errors.add(serviceName + " : service not found by browsing nor by monitoring");
                }

                // Execute related tests (?)

            }
            else {
                errors.add(serviceName + " has no reference service");
            }
            
        } catch (Exception e) {
            errors.add("Exception while validating service " + serviceName + ": " + e.getMessage());
        }

        return errors;
    }

    private boolean isFinerWsdlThan(File wsdlFile, File referenceEnvWsdlFile) {
        return true;
        /*try {
            FileInputStream wsdlFin = new FileInputStream(wsdlFile);
            FileInputStream referenceEnvWsdlFin = new FileInputStream(referenceEnvWsdlFile);
            byte[] referenceEnvBuf = new byte[1024];
            byte[] buf = new byte[1024];
            int referenceEnvNbRead, nbRead;
            while ((referenceEnvNbRead = referenceEnvWsdlFin.read(referenceEnvBuf)) > 0) {
                nbRead = wsdlFin.read(buf);
                if (nbRead != referenceEnvNbRead) {
    
                }
            }
        } catch (IOException ioex) {
            return false;
        }
        return true;*/
    }
    
    public DocumentModelList getAllServices(CoreSession session, DocumentModel model) throws ClientException {
        DocumentModelList services = new DocumentModelListImpl();
        for (DocumentModel child : session.getChildren(model.getRef(), null, new DeletedDocumentFilter(), null)) {
            if (child.getType().equals(Service.DOCTYPE)) {
                services.add(child);
            }
            else {
                services.addAll(getAllServices(session, child));
            }
        }
        return services;
    }

}
