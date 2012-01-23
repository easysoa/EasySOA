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

import org.nuxeo.frascati.NuxeoFraSCAtiException;
import org.ow2.frascati.util.reflect.ReflectionHelper;
import org.easysoa.frascati.processor.intent.ParserIntentObserver;

public interface FraSCAtiServiceItf {

	/**
	 * Closes the FraSCAtiCompositeItf embedded FraSCAti composite instance
	 * 
	 * @param component
	 * 	the FractaComponentItf of the Fractal Component to close
	 */
	void close(FraSCAtiCompositeItf component);

	/**
	 * Removes from FraSCAti the FraSCAtiCompositeItf embedded FraSCAti composite instance which name
	 * is passed as parameter
	 *
	 * @param composite
	 * 	the name of the component to remove
	 * @throws NuxeoFraSCAtiException
	 * 	if the component doesn't exist
	 */
	void remove(String composite) throws NuxeoFraSCAtiException;
	
	/**
	 * Returns the FraSCAtiCompositeItf embedded FraSCAti composite instance which 
	 * name is passed as parameter
	 * 
	 * @param composite
	 * 	the name of the component to search
	 * @return
	 * 	FraSCAtiCompositeItf embedded Fractal Component
	 * @throws NuxeoFraSCAtiException
	 * 	if the component doesn't exist
	 */
	FraSCAtiCompositeItf getComposite(String composite) throws NuxeoFraSCAtiException ;
	
	/**
	 * Process a contribution ZIP archive.
	 *
	 * @param contribution 
	 * 	name of the contribution file to load.
	 * @param processingContext 
	 * 	the ProcessingContext to use
	 * @return 
	 * 	an array of FraSCAtiCompositeItf embedded loaded composites.
	 * @throws NuxeoFraSCAtiException 
	 * 	if no component can be found in the contribution file
	 */
	FraSCAtiCompositeItf[] processContribution(String contribution,AbstractProcessingContext processingContext) 
			throws NuxeoFraSCAtiException;
	
	/**
	 * Process a contribution ZIP archive.
	 *
	 * @param contribution
	 * 	name of the contribution file to load.
	 * @param processingMode
	 * 	the ProcessingModeProxy constant to use to build a FraSCAti ProcessingContext
	 * @param urls
	 * 	a set of URLs to build the FraSCAti ProcessingContext
	 * @return
	 * 	an array of FraSCAtiCompositeItf embedded loaded composites.
	 * @throws NuxeoFraSCAtiException
	 * 	if no component can be found in the contribution file
	 */
	FraSCAtiCompositeItf[] processContribution(String contribution,	int processingMode, URL... urls) 
			throws NuxeoFraSCAtiException;
	

	/**
	 * Process a contribution ZIP archive, using the ProcessingMode.all mode
	 * 
	 * @param contribution
	 * 	name of the contribution file to load.
	 * @return
	 * 	an array of FraSCAtiCompositeItf embedded loaded composites.
	 * @throws NuxeoFraSCAtiException
	 * 	if no component can be found in the contribution file
	 */
	FraSCAtiCompositeItf[] processContribution(String contribution)
			throws NuxeoFraSCAtiException;

	/**
	 * Loads an SCA composite and create the associate FraSCAti composite instance.
	 * 
	 * @param composite
	 * 	name of the composite file to load.
	 * @param processingContext
	 * 	the ProcessingContext to use
	 * @return
	 * 	the FraSCAtiCompositeItf embedded resulting FraSCAti composite instance.
	 * @throws NuxeoFraSCAtiException
	 * 	if the composite can not be loaded
	 */
	FraSCAtiCompositeItf processComposite(String composite,AbstractProcessingContext processingContext) throws NuxeoFraSCAtiException;

	/**
	 * Loads an SCA composite and create the associate FraSCAti composite instance.
	 * 
	 * @param composite
	 * 	name of the composite file to load.
	 * @param processingMode
	 * 	the ProcessingModeProxy constant to use to build a FraSCAti ProcessingContext
	 * @param urls
	 * 	a set of URLs to build the FraSCAti ProcessingContext
	 * @return
	 * 	the FraSCAtiCompositeItf embedded resulting FraSCAti composite instance.
	 * @throws NuxeoFraSCAtiException
	 * 	if the composite can not be loaded
	 */	
	FraSCAtiCompositeItf processComposite(String composite,int processingMode, URL...urls) throws NuxeoFraSCAtiException;
	
	/**
	 * Loads an SCA composite and create the associate FraSCAti composite instance,
	 * using the ProcessingMode.all mode.
	 * 
	 * @param composite
	 * 	name of the composite file to load
	 * @return
	 * 	the FraSCAtiCompositeItf embedded resulting FraSCAti composite instance.
	 * @throws NuxeoFraSCAtiException
	 * 	if the composite can not be loaded
	 */	
	FraSCAtiCompositeItf processComposite(String composite)	throws NuxeoFraSCAtiException;
	
	/**
	 * Creates a new ReflectionHelper embedded ProcessingContext instance.
	 *
	 * @param urls 
	 * 	the URLs to add to the class loader of the processing context.
	 * @return 
	 * 	a new ReflectionHelper embedded processing context instance.
	 */
	ReflectionHelper newProcessingContext(URL... urls);
	
	/**
	 * Returns the list of FraSCAtiCompositeItf embedded composites, registered in the root of FraSCAti
	 * 
	 * @return
	 * 	the list of registered composites 
	 */
	FraSCAtiCompositeItf[] getComposites();
	
	/**
	 * Returns the service associated to the component passed as parameter, which 
	 * name and class are also passed as parameter
	 * 
	 * @param component
	 * 	the component to look the service in
	 * @param serviceName
	 * 	the service name
	 * @param serviceClass
	 * 	the service class
	 * @return
	 * 	the service instance
	 * @throws NuxeoFraSCAtiException
	 * 	if the service has not been found
	 */
	<T> T getService(Object component,String serviceName,Class<T> serviceClass) 
			throws NuxeoFraSCAtiException;
	
	/**
	 * Returns the FraSCAtiCompositeItf embedded top level domain composite 
	 * of the FraSCati instance
	 *
	 * @return 
	 * 	FraSCAtiCompositeItf embedded top level domain composite.
	 */
	FraSCAtiCompositeItf getTopLevelDomainComposite();

	
	/**
	 * Adds a ParserIntentObserver which newComposite method will be call
	 * each time the FraSCAti instance will load a new composite
	 * 
	 * @param observer
	 * 		the observer to add to the observers list
	 */
	void addParserIntentObserver(ParserIntentObserver observer);
	
	/**
	 * Allow to unregister a servlet binded by an SCA component
	 * 
	 * @param URL
	 * 		the URL of the servlet to unregister
	 */
	void unregisterServlet(URL url);
}
