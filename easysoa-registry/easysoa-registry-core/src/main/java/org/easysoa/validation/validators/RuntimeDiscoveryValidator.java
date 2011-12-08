package org.easysoa.validation.validators;

import java.util.LinkedList;
import java.util.List;

import org.easysoa.doctypes.Service;
import org.easysoa.validation.ServiceValidator;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;

/**
 * 
 * Service validator that checks if given service has been discovered at runtime
 * 
 * @author mkalam-alami, mdutoo
 *
 */
public class RuntimeDiscoveryValidator extends ServiceValidator {

    @Override
    public List<String> validateService(CoreSession session, DocumentModel service, DocumentModel referenceService) {
        List<String> errors = new LinkedList<String>();
        try {
            // check discovery (either by browsing or by monitoring) TODO restrict to runtime env only
            String discoveryTypeBrowsing = (String) referenceService.getProperty(Service.SCHEMA_COMMON, Service.PROP_DTBROWSING);
            String discoveryTypeMonitoring = (String) referenceService.getProperty(Service.SCHEMA_COMMON, Service.PROP_DTMONITORING);
            if (discoveryTypeBrowsing == null || discoveryTypeBrowsing.isEmpty()
                    && (discoveryTypeMonitoring == null || discoveryTypeMonitoring.isEmpty())) {
                errors.add("Service not found by browsing nor by monitoring");
            }
        } catch (Exception e) {
            errors.add("Exception while validating service: " + e.getMessage());
        }
        
        return errors;
    }

}
