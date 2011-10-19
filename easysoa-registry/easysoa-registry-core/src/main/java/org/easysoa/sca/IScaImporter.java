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

	/**
	 * Import a SCA
	 */
	public void importSCA() throws Exception;
	
	/**
	 * 
	 * @param appliImplModel
	 */
	public void setParentAppliImpl(DocumentModel appliImplModel);
	
	/**
	 * 
	 * @param serviceStackType
	 */
	public void setServiceStackType(String serviceStackType);
	
	/**
	 * 
	 * @param serviceStackUrl
	 */
	public void setServiceStackUrl(String serviceStackUrl);
	
	/**
	 * Returns the Nuxeo document manager
	 * @return
	 */
    public CoreSession getDocumentManager();

    /**
     * 
     * @return
     */
    public Blob getCompositeFile();
    
    /**
     * 
     * @return
     */
    public String getServiceStackType();
    
    /**
     * 
     * @return
     */
    public String getServiceStackUrl();
    
    /**
     * 
     * @return
     */
    public DocumentModel getParentAppliImplModel();

    /**
     * 
     * @return
     */
    public String getCurrentArchiName();
    
    /**
     * 
     * @return
     */
    public String toCurrentArchiPath();
    
    /**
     * 
     * @return
     */
    public String getBindingUrl();
}
