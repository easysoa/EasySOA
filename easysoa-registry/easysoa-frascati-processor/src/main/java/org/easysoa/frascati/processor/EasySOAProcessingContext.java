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
package org.easysoa.frascati.processor;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.stp.sca.Composite;
import org.ow2.frascati.assembly.factory.api.ProcessingContext;
import org.ow2.frascati.assembly.factory.api.ProcessingMode;
import org.ow2.frascati.parser.api.ParsingContext;
import org.ow2.frascati.util.FrascatiClassLoader;

public class EasySOAProcessingContext implements ProcessingContext, ParsingContext{
	
	// ---------------------------------------------------------------------------
	// Internal state.
	// --------------------------------------------------------------------------
	/** Logger */
	private static final Logger log = Logger.getLogger(EasySOAProcessingContext.class.getCanonicalName());

	/** the class loader of this parsing context. */
	private ClassLoader classLoader;

	/** structure to store data of this parsing context. */
	private Map<Object, Map<Class<?>, Object>> data = new HashMap<Object, Map<Class<?>, Object>>();

	/** the number of warnings. */
	private int nbWarnings;

	/** the warning messages */
	private List<String> warningMessages = new ArrayList<String>();

	/** the number of errors. */
	private int nbErrors;

	/** the error messages */
	private List<String> errorMessages = new ArrayList<String>();

	/** The current processing mode */
	private ProcessingMode processingMode = ProcessingMode.all;

	/** The processed root SCA composite */
	private Composite rootComposite;

	// ---------------------------------------------------------------------------
	// Public methods.
	// --------------------------------------------------------------------------

	/**
	 * Construct with the current thread context class loader.
	 */
	public EasySOAProcessingContext() {
		this(new FrascatiClassLoader());
	}
	
	/**
	 * Construct with an array of URLs used to create the ClassLoader.
	 * 
	 * @param classLoader
	 *            the urls array for class loader of the parser context.
	 */
	public EasySOAProcessingContext(URL[] urls){		
		this(new FrascatiClassLoader(urls));
	}

	/**
	 * Construct with a class loader.
	 * 
	 * @param classLoader
	 *            the class loader of the parser context.
	 */
	public EasySOAProcessingContext(ClassLoader classLoader) {
		this.classLoader = classLoader;
	}
	
	/**
	 * @see 
	 *      org.ow2.frascati.parser.api.ParsingContext.loadClass(java.lang.String)
	 */
	public final <T> Class<T> loadClass(String className) {
		try {
			return (Class<T>) this.classLoader.loadClass(className);
		} catch (ClassNotFoundException cnfe) {
			if (getResource(className.replace(".", File.separator) + ".java") != null) {
				return null;
			}
			//throw cnfe;
		}
		return null;
	}

	/**
	 * @see ProcessingContext.getProcessingMode()
	 */
	public final ProcessingMode getProcessingMode() {
		return this.processingMode;
	}

	/**
	 * @see ProcessingContext.setProcessingMode(ProcessingMode)
	 */
	public final void setProcessingMode(ProcessingMode processingMode) {
		this.processingMode = processingMode;
	}

	/**
	 * @see ProcessingContext.getRootComposite()
	 */
	public final Composite getRootComposite() {
		return this.rootComposite;
	}

	/**
	 * @see ProcessingContext.setRootComposite(Composite)
	 */
	public final void setRootComposite(Composite composite) {
		this.rootComposite = composite;
	}

	/**
	 * @see ParsingContext#getClassLoader()
	 */
	public final ClassLoader getClassLoader() {
		return this.classLoader;
	}

	/**
	 * @see ParsingContext#getResource(String)
	 */
	public final URL getResource(String name) {
		return this.classLoader.getResource(name);
	}

	/**
	 * @see ParsingContext#putData(Object, Class, T)
	 */
	public final <T> void putData(Object key, Class<T> type, T data) {
		Map<Class<?>, Object> data4key = this.data.get(key);
		if (data4key == null) {
			data4key = new HashMap<Class<?>, Object>();
			this.data.put(key, data4key);
		}
		data4key.put(type, data);
	}

	/**
	 * @see ParsingContext#getData(Object, Class)
	 */
	@SuppressWarnings("unchecked")
	public final <T> T getData(Object key, Class<T> type) {
		Map<Class<?>, Object> data4key = this.data.get(key);
		if (data4key == null) {
			return null;
		}
		return (T) data4key.get(type);
	}

	/**
	 * @see ParsingContext#warning(String)
	 */
	public void warning(String message) {
		warningMessages.add(message);
		log.warning(message);
		this.nbWarnings++;
	}

	/**
	 * @see ParsingContext#getWarnings()
	 */
	public final int getWarnings() {
		return this.nbWarnings;
	}

	public void error(String message) {
		errorMessages.add(message);
		log.severe(message);
		this.nbErrors++;
	}

	/**
	 * @see ParsingContext#getErrors()
	 */
	public final int getErrors() {
		return this.nbErrors;
	}

	/**
	 * @see ParsingContext#getLocationURI(EObject)
	 */
	public String getLocationURI(EObject eObject) {
		URI uri = getData(eObject, URI.class);
		return uri == null ? null : uri.toString();
	}

	// ////////////////////////////////////////////
	// additional methods

	public List<String> getWarningMessages() {
		return warningMessages;
	}

	public List<String> getErrorMessages() {
		return errorMessages;
	}

	// ////////////////////////////////////////////

}
