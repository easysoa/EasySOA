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
import java.util.logging.Logger;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.stp.sca.Composite;
import org.nuxeo.frascati.NuxeoFraSCAtiException;
import org.ow2.frascati.util.reflect.ReflectionHelper;


/**
 * Abstract implementation of the FraSCAtiProcessingContextItf
 * AbstractProcessingContext allow to interact with a FraSCAti ProcessingContext instance
 */
public abstract class AbstractProcessingContext  implements FraSCAtiProcessingContextItf{
	
	protected ReflectionHelper delegate;
	protected static final Logger log = Logger.getLogger(
			AbstractProcessingContext.class.getCanonicalName());	
	
	/**
	 * Constructor
	 * 
	 * @param delegate
	 * 	the ReflectionHelper embedded ProcessingContext associated to 
	 * 	the AbstractProcessingContext instance
	 * @throws NuxeoFraSCAtiException 
	 * 	if the delegate or its embedded FraSCAti ProcessingContext is null
	 */
	public AbstractProcessingContext(ReflectionHelper delegate) throws NuxeoFraSCAtiException {
		if(delegate == null || delegate.get() == null){
			throw new NuxeoFraSCAtiException("Enable to instantiate an AbstractProcessingContext : delegate FraSCati ProcessnigContext is null");
		}
		this.delegate = delegate;
	}

	//////////////////////////////////////////////
	// additional methods
	
	/* (non-Javadoc)
	 * @see org.nuxeo.frascati.api.FraSCAtiProcessingContextItf#getWarningMessages()
	 */
	@Override
	public List<String> getWarningMessages() {
		return  (List<String>) delegate.invoke("getWarningMessages");
	}

	/* (non-Javadoc)
	 * @see org.nuxeo.frascati.api.FraSCAtiProcessingContextItf#getErrorMessages()
	 */
	@Override
	public List<String> getErrorMessages() {
		return  (List<String>) delegate.invoke("getErrorMessages");
	}

	//////////////////////////////////////////////
	// delegate enhanced methods	
	
	/* (non-Javadoc)
	 * @see org.nuxeo.frascati.api.FraSCAtiProcessingContextItf#getClassLoader()
	 */
	@Override
	public ClassLoader getClassLoader() {
		return (ClassLoader) delegate.invoke("getClassLoader");
	}

	/* (non-Javadoc)
	 * @see org.nuxeo.frascati.api.FraSCAtiProcessingContextItf#getProcessingMode()
	 */
	@Override
	public int getProcessingMode() {
		return ProcessingModeProxy.convert(delegate.invoke("getProcessingMode"));
	}

	/* (non-Javadoc)
	 * @see org.nuxeo.frascati.api.FraSCAtiProcessingContextItf#loadClass(java.lang.String)
	 */
	@Override
	public <T> Class<T> loadClass(String className) throws ClassNotFoundException {
		Class<T> clazz = (Class<T>) delegate.invoke("loadClass",
				new Class<?>[]{String.class},
				new Object[]{className});	
//		if(clazz == null){
//			throw new ClassNotFoundException(className);
//		}
		return clazz;
	}

	/* (non-Javadoc)
	 * @see org.nuxeo.frascati.api.FraSCAtiProcessingContextItf#setProcessingMode(int)
	 */
	@Override
	public void setProcessingMode(int mode) {
		delegate.invoke("setProcessingMode",
				new Class<?>[]{ProcessingModeProxy.getProcessingModeClass()},
				new Object[]{ProcessingModeProxy.getProcessingMode(mode)});;
	}

	/* (non-Javadoc)
	 * @see org.nuxeo.frascati.api.FraSCAtiProcessingContextItf#getResource(java.lang.String)
	 */
	@Override
	public URL getResource(String name) {
		return (URL) delegate.invoke("getResource",
				new Class<?>[]{String.class},
				new Object[]{name});
	}

	/* (non-Javadoc)
	 * @see org.nuxeo.frascati.api.FraSCAtiProcessingContextItf#getRootComposite()
	 */
	@Override
	public Composite getRootComposite() {
		Composite root = (Composite) 
				delegate.invoke("getRootComposite");
		return root;
	}

	/* (non-Javadoc)
	 * @see org.nuxeo.frascati.api.FraSCAtiProcessingContextItf#setRootComposite(org.eclipse.stp.sca.Composite)
	 */
	@Override
	public void setRootComposite(Composite composite) {
		delegate.invoke("setRootComposite",
				new Class<?>[]{Composite.class},
				new Object[]{composite});
	}

	/* (non-Javadoc)
	 * @see org.nuxeo.frascati.api.FraSCAtiProcessingContextItf#warning(java.lang.String)
	 */
	@Override
	public void warning(String message) {
		delegate.invoke("warning",
				new Class<?>[]{String.class},
				new Object[]{message});
	}

	/* (non-Javadoc)
	 * @see org.nuxeo.frascati.api.FraSCAtiProcessingContextItf#getWarnings()
	 */
	@Override
	public int getWarnings() {
		return (Integer)delegate.invoke("getWarnings");
	}

	/* (non-Javadoc)
	 * @see org.nuxeo.frascati.api.FraSCAtiProcessingContextItf#error(java.lang.String)
	 */
	@Override
	public void error(String message) {
		delegate.invoke("error",
				new Class<?>[]{String.class},
				new Object[]{message});
	}

	/* (non-Javadoc)
	 * @see org.nuxeo.frascati.api.FraSCAtiProcessingContextItf#getErrors()
	 */
	@Override
	public int getErrors() {		
		return (Integer)delegate.invoke("getErrors");
	}

	/* (non-Javadoc)
	 * @see org.nuxeo.frascati.api.FraSCAtiProcessingContextItf#getLocationURI(org.eclipse.emf.ecore.EObject)
	 */
	@Override
	public String getLocationURI(EObject eObject) {
		return (String) delegate.invoke("getLocationURI",
				new Class<?>[]{EObject.class},
				new Object[]{eObject});
	}
	

	/**
	 * Returns the ProcessingContext associated to the 
	 * AbstractProcessingContext instance 
	 * 
	 * @return 
	 * 		delegate ProcessingContext
	 */
	public Object getProcessingContext() {
		return delegate.get();
	}
	


}
