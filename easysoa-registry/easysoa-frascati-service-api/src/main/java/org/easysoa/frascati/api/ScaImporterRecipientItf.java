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

import java.util.Stack;
import org.eclipse.emf.ecore.EObject;

/**
 * SCA Importer on the FraSCAti's side
 */
public interface ScaImporterRecipientItf
{
    /**
     * Return the stack where browsed composite elements' names are piled up
     * 
     * @return
     *      the browsed elements' names stack
     */
    Stack<String> getArchiNameStack();
    
    /**
     * Return the stack where browsed composite elements' are piled up
     * 
     * @return
     *      the browsed elements stack
     */
    Stack<EObject> getBindingStack();
    
    /**
     * Initiate to visit a Service Binding Element
     */
    void runtimeServiceBindingVisit();
    
    /**
     * Initiate to visit a Reference Binding Element
     */
    void runtimeReferenceBindingVisit();
}
