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

package org.easysoa.sca;

import java.io.File;
import org.easysoa.sca.visitors.ScaVisitor;

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
    // TODO Not really a good solution, do better	
	//public void setParentAppliImpl(DocumentModel appliImplModel);
	public void setParentAppliImpl(Object appliImplModel);
	
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
    // TODO Not really a good solution, do better
	//public CoreSession getDocumentManager();
	//public Object getDocumentManager();

    /**
     * 
     * @return
     */
    public File getCompositeFile();
    
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
    public String getCurrentArchiName();
    
    /**
     * 
     * @return
     */
    public String toCurrentArchiPath();
    
    /**
     * Returns a model property
     * @param arg0
     * @param arg1
     * @return
     */
    public String getModelProperty(String arg0, String arg1) throws Exception;
    
    /**
     * creates and returns a ServiceBindingVisitor
     * @return A <code>ScaVisitor<code>
     */
    public ScaVisitor createServiceBindingVisitor();

    /**
     * creates and returns a ReferenceBindingVisitor
     * @return A <code>ScaVisitor<code>
     */
    public ScaVisitor createReferenceBindingVisitor();    
    
}
