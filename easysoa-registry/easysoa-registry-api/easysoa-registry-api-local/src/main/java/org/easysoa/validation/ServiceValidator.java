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

package org.easysoa.validation;

import java.util.List;

import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;

/**
 * 
 * Class to be implemented by each service validator contributed to the ServiceValidatorComponent.
 * 
 * @author mkalam-alami
 *
 */
public abstract class ServiceValidator {
    
    private String name = null;
    
    private String label = null;
    
    /**
     * Runs a validation of a service against a referenced service.
     * Contract: Both models are guaranteed not to be null
     * @param session
     * @param service
     * @param referenceService
     * @return A list of errors OR an empty list if no error
     * @throws ClientException Any exception that prevents the validation to be run
     */
    public abstract List<String> validateService(CoreSession session, DocumentModel service, DocumentModel referenceService) throws Exception;

    public String getName() {
        return this.name;
    }
    
    public void setName(String name) {
        this.name = name;
    }

    public Object getLabel() {
        return this.label;
    }
    
    public void setLabel(String label) {
        this.label = label;
    }


}
