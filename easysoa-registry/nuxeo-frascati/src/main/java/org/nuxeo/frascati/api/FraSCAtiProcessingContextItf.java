/**
 * EasySOA - Nuxeo FraSCAti
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
package org.nuxeo.frascati.api;

import java.net.URL;
import java.util.List;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.stp.sca.Composite;


public interface FraSCAtiProcessingContextItf {

	/**
	 * Returns the ProcessingMode using the ProcessingModeProxy constants :
	 * 		parse = 0;
	 * 		check = 1;
	 * 		generate = 2 ;
	 * 		compile = 3;
	 * 		instantiate = 4;
	 * 		complete = 5;
	 * 		start = 6;
	 * 		all = 7;
	 */
	int getProcessingMode();

	/**
	 *	Defines the ProcessingMode using the ProcessingModeProxy constants :
	 * 		parse = 0;
	 * 		check = 1;
	 * 		generate = 2 ;
	 * 		compile = 3;
	 * 		instantiate = 4;
	 * 		complete = 5;
	 * 		start = 6;
	 * 		all = 7;
	 * @param processingMode
	 * 		an integer chosen in the ProcessingModeProxy set
	 */
	void setProcessingMode(int processingMode);

	/**
	 * Returns the ClassLoader associated to the ProcessingContext
	 */
	ClassLoader getClassLoader();

	/**
	 * Calls the {@link ClassLoader#getResources(String)} method of the delegate
	 * ProcessingContext ClassLoader
	 * 
	 * @param name
	 * 		the name of the search resource
	 * @return
	 * 		the URL to the resource or null if it has not been found
	 */
	URL getResource(String name);

	/**
	 * Returns the root composite of the delegate ProcessingContext
	 * 
	 * @return
	 * 	the root composite associated to the ProcessingContext
	 */
	Composite getRootComposite();

	/**
	 * Defines the root composite of the delegate ProcessingContext
	 * 
	 * @param composite
	 * 	the root composite associated to the ProcessingContext
	 */
	void setRootComposite(Composite composite);

	/**
	 * Calls the {@link ClassLoader#loadClass(String)} method of the delegate
	 * ProcessingContext ClassLoader
	 * 
	 * @param className
	 * 	the searched class name
	 * @return
	 * 	the class
	 * @throws ClassNotFoundException
	 * 	if the class has not been found
	 */
	<T> Class<T> loadClass(String className) throws ClassNotFoundException;

	/**
	 * Registers a new warning
	 *  
	 * @param message
	 * 	the warning message
	 */
	void warning(String message);

	/**
	 * Returns the path of the EObject passed as parameter through EMF hierarchy 
	 * 
	 * @param eObject
	 * 	the EObject which location has to be found 
	 * @return
	 * 	the path
	 */
	String getLocationURI(EObject eObject);

	/**
	 * Returns the number of errors registered, or generated while processing a composite
	 * 
	 * @return
	 * 	the number of errors
	 */
	int getErrors();

	/**
	 * Registers a new error
	 *  
	 * @param message
	 * 	the error message
	 */
	void error(String message);

	/**
	 * Returns the number of warnings registered, or generated while processing a composite
	 * 
	 * @return
	 * 	the number of warnings
	 */
	int getWarnings();

	/**
	 * Returns the list of warning messages registered
	 * 
	 * @return
	 * 	the list of warning messages
	 */
	List<String> getWarningMessages();

	/**
	 * Returns the list of error messages registered
	 * 
	 * @return
	 * 	the list of error messages
	 */
	List<String> getErrorMessages();

}
