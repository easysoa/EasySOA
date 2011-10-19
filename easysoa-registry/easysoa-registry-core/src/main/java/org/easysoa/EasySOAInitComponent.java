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

package org.easysoa;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.easysoa.demo.UserInit;
import org.nuxeo.ecm.core.api.repository.Repository;
import org.nuxeo.ecm.core.api.repository.RepositoryManager;
import org.nuxeo.runtime.api.Framework;
import org.nuxeo.runtime.model.ComponentContext;
import org.nuxeo.runtime.model.DefaultComponent;

/**
 * Component loaded on Nuxeo startup
 * 
 * @author mkalam-alami
 * 
 */
public class EasySOAInitComponent extends DefaultComponent {

    private static Log log = LogFactory.getLog(EasySOAInitComponent.class);

    public void activate(ComponentContext context) throws Exception {

        RepositoryManager repoService = Framework.getService(RepositoryManager.class);

        Repository defaultRepository = repoService.getDefaultRepository();

        // Init default domain
        try {
            new DomainInit(defaultRepository.getName()).runUnrestricted();
        } catch (Exception e) {
            log.warn("Failed to access default repository for initialization: " + e.getMessage());
        }

        try {
            new UserInit(defaultRepository.getName()).runUnrestricted(); // Demo: Init users
        } catch (Exception e) {
            log.warn("Failed to initialize groups: " + e.getMessage());
        }

    }

}