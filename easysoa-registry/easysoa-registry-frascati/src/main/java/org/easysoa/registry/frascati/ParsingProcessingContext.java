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

package org.easysoa.registry.frascati;


import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.stp.sca.Composite;
import org.ow2.frascati.assembly.factory.api.ProcessingContext;
import org.ow2.frascati.assembly.factory.api.ProcessingMode;


/**
 * ProcessingContext for parsing-only purpose
 * wrapping FraSCAti's original, runtime-focused one (ProcessingContextImpl,
 * built by frascati.getCompositeManager() i.e. AssemblyFactoryManager)
 * 
 * - Unfound classes produce warnings rather than errors
 * 
 * NB. can't extend rather than delegate because methods are final
 * @author mdutoo
 *
 */

public class ParsingProcessingContext implements ProcessingContext {
	
	protected ProcessingContext delegate;
	protected List<String> warningMessages = new ArrayList<String>();
	protected List<String> errorMessages = new ArrayList<String>();
	
	public ParsingProcessingContext(ProcessingContext delegate) {
		this.delegate = delegate;
	}
	

	//////////////////////////////////////////////
	// additional methods
	
	public List<String> getWarningMessages() {
		return warningMessages;
	}

	public List<String> getErrorMessages() {
		return errorMessages;
	}


	//////////////////////////////////////////////
	// delegate enhanced methods

	public <T> Class<T> loadClass(String className) throws ClassNotFoundException {
		try {
			return delegate.loadClass(className);
		} catch (Exception e) {
	          this.warning("Java class (interface.java, implementation.java...) '" + className + "' not found");
	          return null;
		}
	}

	public URL getResource(String name) {
		// TODO possible alternatives :
		// use special parsing mode as for loadClass,
		// resolve resource within easysoa registry (other composites, WSDLs...),
		// or even stack unresolved ones for resolution once the target resources have been loaded...
		return delegate.getResource(name);
	}
	
	
	//////////////////////////////////////////////
	// delegate methods

	public ClassLoader getClassLoader() {
		return delegate.getClassLoader();
	}

	public ProcessingMode getProcessingMode() {
		return delegate.getProcessingMode();
	}

	public void setProcessingMode(ProcessingMode processingMode) {
		delegate.setProcessingMode(processingMode);
	}

	public Composite getRootComposite() {
		return delegate.getRootComposite();
	}

	public void setRootComposite(Composite composite) {
		delegate.setRootComposite(composite);
	}

	public <T> void putData(Object key, Class<T> type, T data) {
		delegate.putData(key, type, data);
	}

	public <T> T getData(Object key, Class<T> type) {
		return delegate.getData(key, type);
	}

	public void warning(String message) {
		delegate.warning(message);//
		warningMessages.add(message);
	}

	public int getWarnings() {
		return delegate.getWarnings();
	}

	public void error(String message) {
		delegate.error(message);//
		errorMessages.add(message);
	}

	public int getErrors() {
		return delegate.getErrors();
	}

	public String getLocationURI(EObject eObject) {
		return delegate.getLocationURI(eObject);
	}

}