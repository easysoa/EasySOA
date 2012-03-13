/**
 * EasySOA - FraSCAti
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
package org.easysoa.frascati.api;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.stp.sca.Binding;

/**
 * SCA Importer on the FraSCAti's side
 */
public interface ScaImporterIntermediaryItf
{
    /**
     * Push the name on the top of the archiNameStack stack and 
     * the eObject on the top of the bindingStack stack
     *  
     * @param name
     *          the name to push
     * @param eObject
     *          the EObject to push
     */
    void pushArchi(String name,EObject eObject);
    
    /**
     * Push the Binding object passed on as parameter on the top 
     * of the bindingStack stack and call the importSCA method
     * 
     * @param binding
     */
    void pushBinding(Binding binding);
    
    /**
     * Remove the name from the top of the archiNameStack stack and the 
     * top element (which name is normally the one passed on as 
     * a parameter) of the bindingStack stack
     *  
     * @param name
     *          the name of the EObject to remove from stacks
     */
    void popArchi(String name);
    

    /**
     * Define the ScaImporterRecipientItf object for processing process
     * If a not null ScaImporterRecipientItf is defined, sca import at runtime 
     * is activated (must be defined before each processing process)
     * 
     * @param recipient
     *          the ScaImporterRecipientItf object to use
     */
    void setScaImporterRecipient(ScaImporterRecipientItf recipient);
    
    /**
     * Import Sca Binding at runtime
     * 
     * @throws Exception
     */
    void importSCA() throws Exception;

    /**
     * 
     */
    void defineServiceDelegate();

    /**
     * 
     */
    void defineReferenceDelegate();

    /**
     * 
     */
    void clearDelegate();
    

}
