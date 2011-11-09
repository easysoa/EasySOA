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

package org.easysoa.sca.extension;

import java.io.File;
import java.io.InvalidClassException;
import java.util.LinkedList;
import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.easysoa.sca.IScaImporter;
import org.nuxeo.ecm.core.api.Blob;
import org.nuxeo.ecm.core.api.CoreSession;
import org.nuxeo.runtime.model.ComponentInstance;
import org.nuxeo.runtime.model.ComponentName;
import org.nuxeo.runtime.model.DefaultComponent;

/**
 * 
 * @author jguillemotte
 */
public class ScaImporterComponent extends DefaultComponent {

    public static final ComponentName NAME = new ComponentName(ComponentName.DEFAULT_TYPE, "org.easysoa.sca.extension.ScaImporterComponent");

    private static Log log = LogFactory.getLog(ScaImporterComponent.class);
	
    private List<Class<?>> scaImporterClasses = new LinkedList<Class<?>>();

    public List<Class<?>> getScaImporterClasses() {
        return scaImporterClasses;
    }    
    
	@Override
    public void registerContribution(Object contribution, String extensionPoint, ComponentInstance contributor) throws Exception {
        try {
        	log.debug("registering contribution ..." + extensionPoint);
            synchronized (scaImporterClasses) {
            	if (extensionPoint.equals("scaImporters")) {
            		ScaImporterDescriptor scaImporterDescriptor = (ScaImporterDescriptor) contribution;
        			checkAndSetScaImporter(scaImporterDescriptor, contributor);
            	}
            }
        }
        catch (Exception ex) {
            throw new Exception(renderContributionError(contributor, ""), ex);
        }
	}
	
	@Override
	public void unregisterContribution(Object contribution, String extensionPoint, ComponentInstance contributor){
        synchronized (scaImporterClasses) {
            if (extensionPoint.equals("scaImporters")) {
            	ScaImporterDescriptor scaImporterDescriptor = (ScaImporterDescriptor) contribution;
                if (scaImporterDescriptor.enabled) {
                    try {
                        Class<?> scaImporterClass = Class.forName(scaImporterDescriptor.implementation.trim());                    	
                        for (Class<?> currentScaImporterClass : scaImporterClasses) {
                            if (currentScaImporterClass.equals(scaImporterClass)) {
                            	scaImporterClasses.remove(scaImporterClass);
                            }
                        }
                    }
                    catch (Exception ex) {
                        log.warn("Failed to unregister contribution: " + ex.getMessage());
                    }
                }
            }
        }
	}
	
	/**
	 * 
	 * @param scaImporterDescriptor
	 * @param contributor
	 * @throws Exception
	 */
    public void checkAndSetScaImporter(ScaImporterDescriptor scaImporterDescriptor, ComponentInstance contributor) throws Exception {
		if (scaImporterDescriptor.enabled) {
			Class<?> scaImporterClass = Class.forName(scaImporterDescriptor.implementation.trim());
			log.debug("scaImporterClass = " + scaImporterClass);
			if (IScaImporter.class.isAssignableFrom(scaImporterClass)) {
				// trying to create one in order to check that the impl has the required constructor 
				
				// TODO Change this instantiation method ... Not the same constructor for all Sca Importers   
				//scaImporterClass.getConstructor(new Class[]{ CoreSession.class, Blob.class }).newInstance(new Object[] { null, null });
				scaImporterClass.getConstructor(new Class[]{ CoreSession.class, File.class }).newInstance(new Object[] { null, null });
				
				this.scaImporterClasses.add(scaImporterClass);
			} else {
				log.debug("Check and set Sca importer error");
				throw new InvalidClassException(renderContributionError(contributor, "class " + scaImporterDescriptor.implementation
	                    + " is not an instance of " + IScaImporter.class.getName()));
			}
		}
    }
	
    /**
     * Creates a SCA Importer
     * @param documentManager
     * @param compositeFile
     * @return
     */
    //public IScaImporter createScaImporter(CoreSession documentManager, Blob compositeFile) {
    public IScaImporter createScaImporter(CoreSession documentManager, File compositeFile) {
		try {
			log.debug("scaImporterClasses.size() = " + scaImporterClasses.size());
			Class<?> scaImporterClass = this.scaImporterClasses.get(scaImporterClasses.size() - 1);
			
			// TODO Change this instanciation method ... Not the same constructor for all Sca Importers			
			return (IScaImporter) scaImporterClass.getConstructor(new Class[]{ CoreSession.class, File.class })
					.newInstance(new Object[] { documentManager, compositeFile });
			
		} catch (Exception ex) {
			log.error("An error occurs during the creation of SCA importer", ex);
			// TODO log "error creating sca import, bad config, see init logs"
		}
		return null;
    }
	
	/**
	 * 
	 * @param contributor
	 * @param message
	 * @return
	 */
    private String renderContributionError(ComponentInstance contributor,
            String message) {
        return "Invalid contribution from '" + contributor.getName() + "': " + message;
    }	
	
}
