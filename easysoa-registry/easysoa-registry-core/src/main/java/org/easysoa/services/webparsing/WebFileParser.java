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

package org.easysoa.services.webparsing;

import java.util.Map;

import org.nuxeo.ecm.core.api.Blob;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;

/**
 * 
 * @author mkalam-alami
 *
 */
public interface WebFileParser {
    
    /**
     * Parse the given blob to store information in the given model.
     * 
     * @param session A valid core session.
     * @param data The file data to parse.
     * @param model A model on which to store information. Can be null: in that case, the parser
     * can guess which document model(s) to edit by using the extracted information. Any other document
     * than the one passed in parameter must be saved manually by the parser. 
     * @param options Any options given by the parsing pool consumer.
     */
    void parse(CoreSession session, Blob data, DocumentModel model, Map<String, String> options);

}
