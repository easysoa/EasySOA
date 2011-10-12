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
 * Contact : easysoa-dev@groups.google.com
 */

package org.easysoa.sca;

import javax.xml.stream.XMLStreamReader;

import org.nuxeo.ecm.core.api.Blob;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.ecm.core.api.DocumentModel;


/**
 * Introduced to ease adding another (notably FraSCAti-based) SCA importer in addition to the
 * original XML-based one.
 * 
 * @author mdutoo
 *
 */
public interface IScaImporter {

    public CoreSession getDocumentManager();
    /** TODO alas still a tight coupling with the original XML-based importer */
    public XMLStreamReader getCompositeReader();

    public Blob getCompositeFile();
    public String getServiceStackType();
    public String getServiceStackUrl();
    public DocumentModel getParentAppliImplModel();

    public String getCurrentArchiName();
    public String toCurrentArchiPath();
}
