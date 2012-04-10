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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.nuxeo.ecm.core.api.ClientException;
import org.nuxeo.ecm.core.api.DocumentModel;
import org.nuxeo.ecm.core.api.Filter;

/**
 * Filters all deleted documents.
 * 
 * @author mkalam-alami
 *
 */
public class DeletedDocumentFilter implements Filter {

    private static final long serialVersionUID = 1L;
    
    private static final Log log = LogFactory.getLog(DeletedDocumentFilter.class);
    
    @Override
    public boolean accept(DocumentModel docModel) {
        try {
            return !("deleted".equals(docModel.getCurrentLifeCycleState()));
        } catch (ClientException e) {
            log.warn("Failed to get current document lifecycle state", e);
            return false;
        }
    }
    

}
