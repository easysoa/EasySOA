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

package org.easysoa.services;

import java.util.List;
import java.util.SortedSet;

import org.easysoa.validation.CorrelationMatch;
import org.easysoa.validation.ServiceValidator;
import org.easysoa.validation.ValidationResultList;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;

/**
 * 
 * @author mkalam-alami
 *
 */
public interface ServiceValidationService {
    
    /**
     * Validates all of the services contained in the given document
     * (or the one service given in parameter), against the workspace's reference environment. 
     * @param session
     * @param model
     * @return A list of validation errors
     * @throws Exception
     */
	ValidationResultList validateServices(CoreSession session, DocumentModel model) throws Exception;

    /**
     * Finds correlated services from the reference environment of the given service.
     * Note: if the service explicitly references another one, this function will only return
     * that service, with a correlation rate of 1.0 (100%).
     * @param session
     * @param service
     * @return A list of correlation match, ordered in decreasing correlation rate.
     * @throws Exception 
     */
    SortedSet<CorrelationMatch> findCorrelatedServices(CoreSession session, DocumentModel service) throws Exception;
    
    /**
     * 
     * @return The actual validator list: it should be read only
     */
    List<ServiceValidator> getValidators();
    
}
