/**
 * EasySOA Registry
 * Copyright 2011 Open Wide
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * 
 * Contact : easysoa-dev@googlegroups.com
 */

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
        // Check discovery (either by browsing or by monitoring) if the service is expected in the reference environment
        if (referenceService != null) {
            try {
                String discoveryTypeBrowsing = (String) service.getProperty(Service.SCHEMA_COMMON, Service.PROP_DTBROWSING);
                String discoveryTypeMonitoring = (String) service.getProperty(Service.SCHEMA_COMMON, Service.PROP_DTMONITORING);
                if (discoveryTypeBrowsing == null || discoveryTypeBrowsing.isEmpty()
                        && (discoveryTypeMonitoring == null || discoveryTypeMonitoring.isEmpty())) {
                    errors.add("Service not found by browsing nor by monitoring");
                }
            } catch (Exception e) {
                errors.add("Exception while validating service: " + e.getMessage());
            }
        }
        
        return errors;
    }

}
