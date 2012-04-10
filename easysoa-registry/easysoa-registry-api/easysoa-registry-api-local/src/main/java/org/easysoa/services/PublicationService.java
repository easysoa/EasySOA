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

import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;

/**
 * 
 * @author mkalam-alami
 *
 */
public interface PublicationService {
    
    /**
     * Publishes the given document in a certain environment (and recursively all childs) 
     * @param session
     * @param model
     * @param environmentName
     */
    void publish(CoreSession session, DocumentModel model, String environmentName);

    /**
     * Removes the published version of a document in a certain environment
     * @param session
     * @param doc
     */
    void unpublish(CoreSession session, DocumentModel model, String environmentName);
    
    /**
     * Removes all published versions of a document
     * @param session
     * @param doc
     */
    void unpublish(CoreSession session, DocumentModel model);
    
    DocumentModel forkEnvironment(CoreSession session, DocumentModel sectionModel, String newWorkspaceName) throws Exception;

	void updateFromReferenceEnvironment(CoreSession session, DocumentModel appliImplModel) throws Exception;
    
}
